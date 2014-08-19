package com.getsu.wcy

import grails.rest.RestfulController
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder

class PersonController extends RestfulController {

    static responseFormats = ['json']

    PersonController() {
        super(Person)
    }


    def index(String sort, String order, Integer offset, Integer max) {
        def from = offset ?: 0
        def size = Math.min(max ?: 10, 100)

        def searchResults = search(sort, order, from, size)
        searchResults = finishUpIfClose(searchResults, sort, order, from, size)

        respond new PeopleModel(people: searchResults.searchResults, peopleCount: searchResults.total)
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
}

// 'respond' with PagedResultList fails to include totalCount, and doesn't work with Map, so needs this class
class PeopleModel {
    List people
    int peopleCount
    List phoneNumberTypes = PhoneNumber.PhoneNumberType.values()    // for selects
    List connectionTypes = Connection.ConnectionType.values()       // for selects
}
