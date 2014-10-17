package com.getsu.wcy

import grails.rest.RestfulController
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder
import org.grails.plugins.elasticsearch.ElasticSearchService
import org.springframework.web.multipart.MultipartFile
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

import static org.springframework.http.HttpStatus.*

class PersonController extends RestfulController {

    ElasticSearchService elasticSearchService
    def grailsResourceLocator

    static responseFormats = ['json']

    PersonController() {
        super(Person)
    }


    def index(String sort, String order, Integer offset, Integer max) {
        def from = offset ?: 0
        def size = Math.min(max ?: 10, 100)

        def searchResults = search(sort, order, from, size)
        searchResults = finishUpIfClose(searchResults, sort, order, from, size)
        fillInDates(searchResults.searchResults)

        respond new PeopleModel(people: searchResults.searchResults, peopleCount: searchResults.total)
    }

    private static void fillInDates(people) {
        // The birthDates are indexed in elasticsearch as native dates, which would be loaded into java.util.Date.
        // However, if the server and browser are in different time zones,
        // then sending the time component between them would cause errors,
        // and just yyyy-MM-dd fails to bind into java.util.Date, anyway.
        // So, birthDate is java.sql.Date, and needs to be fetched from the db.
        for (p in people) {
            p.birthDate = Person.get(p.id).birthDate
        }
    }

    private search(String sortColumn, String ord, Integer from, Integer size) {
        def sorts = buildSorts(sortColumn, ord == 'asc' ? SortOrder.ASC : SortOrder.DESC)
        Person.search([sort: sorts, from: from, size: size]) {
            if (params.q?.trim()) {
                query_string(query: params.q)
            } else {
                match_all()
            }
            // todo: must(term("userid", userid)) w/ SpringSecurity ACL & history
        }
    }

    private static buildSorts(String sortColumn, SortOrder order) {
        switch (sortColumn) {

            case 'name':
                return buildFieldSorts(name: order)

            case 'preferredEmail':
                return buildFieldSorts(
                        'preferredEmail.connectionType': order,
                        'preferredEmail.address': order,
                        'name': SortOrder.ASC     // secondary sort
                )

            case 'preferredPhone':
                return buildFieldSorts(
                        'preferredPhone.connectionType': order,
                        'preferredPhone.type': order,
                        'preferredPhone.number': order,
                        'name': SortOrder.ASC     // secondary sort
                )

            case 'preferredConnection':
                return buildFieldSorts(
                    'preferredConnection.type': order,
                    'preferredConnection.preferredAddress.countryCode': order,
                    'preferredConnection.preferredAddress.state': order,
                    'preferredConnection.preferredAddress.city': order,
                    'preferredConnection.preferredAddress.postalCode': order,
                    'preferredConnection.preferredAddress.line1': order,
                    'preferredConnection.preferredAddress.line2': order,
                    'name': SortOrder.ASC     // secondary sort
                )

            default:
                return []
        }
    }

    private static buildFieldSorts(Map<String, SortOrder> fields) {
        fields.collect { field, order -> SortBuilders.fieldSort(field).order(order) }
    }

    // If the remainder is 30% or less of the page size, then just get the rest now too, to spare the user the extra request.
    private finishUpIfClose(results, String sort, String order, Integer from, Integer size) {
        from += results.size()
        int remainder = results.total - from
        if (remainder in 1..(size * 0.30)) {
            search(sort, order, from, size).each {results.searchResults.add(it)}
        }
        results
    }

    @Override
    Object update() {
        if(handleReadOnly()) {
            return
        }

        Person instance = (Person) queryForResource(params.id)
        if (instance == null) {
            notFound()
            return
        }

        instance.properties = getParametersToBind()

        deleteBlankElements(instance)

        if (instance.hasErrors()) {
            respond instance.errors, view:'edit' // STATUS CODE 422
            return
        }

        instance.save flush:true

        elasticSearchService.index(instance)   // had problems with auto-index, so doing it explicitly

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: "${resourceClassName}.label".toString(), default: resourceClassName), instance.id])
                redirect instance
            }
            '*'{
                response.addHeader(HttpHeaders.LOCATION,
                        g.createLink(
                                resource: this.controllerName, action: 'show',id: instance.id, absolute: true,
                                namespace: hasProperty('namespace') ? this.namespace : null ))
                respond instance, [status: OK]
            }
        }
    }

    @Override
    Object save() {
        if(handleReadOnly()) {
            return
        }
        def instance = createResource(getParametersToBind())

        instance.validate()
        if (instance.hasErrors()) {
            respond instance.errors, view:'create' // STATUS CODE 422
            return
        }

        instance.save flush:true

        elasticSearchService.index(instance)   // had problems with auto-index, so doing it explicitly

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: "${resourceName}.label".toString(), default: resourceClassName), instance.id])
                redirect instance
            }
            '*' {
                response.addHeader(HttpHeaders.LOCATION,
                        g.createLink(
                                resource: this.controllerName, action: 'show',id: instance.id, absolute: true,
                                namespace: hasProperty('namespace') ? this.namespace : null ))
                respond instance, [status: CREATED]
            }
        }
    }

    void deleteBlankElements(Person p) {
        def blank
        while (blank = p.emailAddresses.find {!it.address}) {
            p.removeFromEmailAddresses(blank)
            blank.delete()
        }
        while (blank = p.phoneNumbers.find {!it.number}) {
            p.removeFromPhoneNumbers(blank)
            blank.delete()
        }
    }

// non-REST-full

    def viewPhoto() {
        log.debug 'called viewPhoto'
        Person p = Person.get(params.id)
        // todo: check access authorization
        if (p?.photo) {
            response.setHeader("Content-disposition", "attachment; filename=${p.photo.fileName}")
            // response.contentType = p.photo.fileType //'image/jpeg' will do too
            response.outputStream << p.photo.contents
            response.outputStream.flush()
        } else {
            def errorImage = 'images/skin/exclamation.png'
            URL url = grailsResourceLocator.findResourceForURI(errorImage).URL
            response.setHeader("Content-disposition", "attachment; filename=${errorImage}")
            response.outputStream << url.bytes
            response.outputStream.flush()
        }
    }

    def uploadPhoto() {
        Person p = Person.get(params.id)
        MultipartFile uploadedFile = request.getFile('file')
        // todo: if (uploadedFile.size > UPLOAD_LIMIT) { result status = error code... }
        def photo = new Photo()
        photo.contents = uploadedFile.bytes
        photo.fileName = getOriginalFileName(uploadedFile)
        assert photo.save()    // redundant w/ cascade on p.save()?
        p.photo = photo
        assert p.save()
        elasticSearchService.index(p)   // had problems with auto-index, so doing it explicitly
        // todo: validate file image format and scale down if too big?
        render 'uploaded photo'
    }

    public static getOriginalFileName(MultipartFile uploadedFile) {
        char otherSeparatorChar = (char) (File.separatorChar == '/' ? '\\' : '/')
        return new File(uploadedFile.originalFilename.replace(otherSeparatorChar, File.separatorChar)).name
    }
}

// 'respond' with PagedResultList fails to include totalCount, and doesn't work with Map, so needs this class
class PeopleModel {
    List people
    int peopleCount
    List phoneNumberTypes = PhoneNumber.PhoneNumberType.values()    // for selects
    List connectionTypes = Connection.ConnectionType.values()       // for selects
}
