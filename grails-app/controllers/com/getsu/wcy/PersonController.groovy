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
        PagedResultList results = Person.list(params)
        results = finishUpIfClose(results)
        respond new PeopleModel(people: results, peopleCount: results.totalCount)
    }

    // If the remainder is 30% or less of the page size, then just get the rest now too, to spare the user the extra request.
    private finishUpIfClose(PagedResultList results) {
        params.offset += results.size()
        int remainder = results.totalCount - params.offset
        if (remainder in 1..(params.max * 0.30)) {
            Person.list(params).each {results.add(it)}
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
