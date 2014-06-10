package com.getsu.wcy

import grails.orm.PagedResultList
import grails.rest.RestfulController

class PersonController extends RestfulController {

    static responseFormats = ['json']

    PersonController() {
        super(Person)
    }


    def index(Integer max, Integer offset) {
        params.max = Math.min(max ?: 10, 100)
        params.offset = offset ?: 0
        params.readOnly = true

        def sort = params.remove('sort')
        def ord = params.remove('order')
        PagedResultList results = listSorted(sort, ord)
        results = finishUpIfClose(results, sort, ord)

        respond new PeopleModel(people: results, peopleCount: results.totalCount)
    }

    private PagedResultList listSorted(sort, ord) {
        Person.createCriteria().list(params) {
            switch (sort) {
                case 'name':
                    order 'name', ord
                    break
                case 'preferredEmail':
                    preferredEmail {
                        order 'connectionType', ord
                        order 'address', ord
                    }
                    order 'name', 'asc'     // secondary sort
                    break
                case 'preferredPhone':
                    preferredPhone {
                        order 'connectionType', ord
                        order 'type', ord
                        order 'number', ord
                    }
                    order 'name', 'asc'     // secondary sort
                    break
                case 'preferredConnection':
                    preferredConnection {
                        order 'type', ord
                        preferredAddress {
                            order 'countryCode', ord
                            order 'state', ord
                            order 'city', ord
                            order 'postalCode', ord
                            order 'line1', ord
                            order 'line2', ord
                        }
                    }
                    order 'name', 'asc'     // secondary sort
                    break
            }
        }
    }

    // If the remainder is 30% or less of the page size, then just get the rest now too, to spare the user the extra request.
    private finishUpIfClose(PagedResultList results, sort, ord) {
        params.offset += results.size()
        int remainder = results.totalCount - params.offset
        if (remainder in 1..(params.max * 0.30)) {
            listSorted(sort, ord).each {results.add(it)}
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
