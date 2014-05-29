package com.getsu.wcy

import grails.converters.JSON
import grails.rest.RestfulController

class PersonController extends RestfulController {

    static responseFormats = ['json']

    PersonController() {
        super(Person)
    }

    @Override
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        JSON.use('deep') {
            respond listAllResources(params), model: [("${resourceName}Count".toString()): countResources()]
        }
    }
}
