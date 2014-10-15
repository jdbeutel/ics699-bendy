package com.getsu.wcy

import grails.rest.RestfulController
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.grails.plugins.elasticsearch.ElasticSearchService

import static org.springframework.http.HttpStatus.OK

class ConnectionController extends RestfulController {

    ElasticSearchService elasticSearchService

    static responseFormats = ['json']

    ConnectionController() {
        super(Connection)
    }


    @Override
    Object update() {
        if(handleReadOnly()) {
            return
        }

        Connection instance = (Connection) queryForResource(params.id)
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

        def person = Person.withCriteria(uniqueResult: true) {  // todo: back reference in Connection?
            connections {
                eq 'id', instance.id
            }
        }
        person.save flush:true      // to update preferred properties if necessary
        elasticSearchService.index(person)   // had problems with auto-index, so doing it explicitly

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

    void deleteBlankElements(Connection c) {
        def blank
        while (blank = c.emailAddresses.find {!it.address}) {
            c.removeFromEmailAddresses(blank)
            blank.delete()
        }
        while (blank = c.place.emailAddresses.find {!it.address}) {
            c.place.removeFromEmailAddresses(blank)
            blank.delete()
        }
        while (blank = c.phoneNumbers.find {!it.number}) {
            c.removeFromPhoneNumbers(blank)
            blank.delete()
        }
        while (blank = c.place.phoneNumbers.find {!it.number}) {
            c.place.removeFromPhoneNumbers(blank)
            blank.delete()
        }
    }
}
