package com.getsu.wcy

import grails.rest.RestfulController
import org.grails.plugins.elasticsearch.ElasticSearchService

class AddressController extends RestfulController {

    ElasticSearchService elasticSearchService

    static responseFormats = ['json']

    AddressController() {
        super(Address)
    }


    @Override
    Object update() {
        super.update()

        def person = Person.withCriteria(uniqueResult: true) {  // todo: back reference in Connection?
            connections {
                place {
                    addresses {
                        eq 'id', params.long('id')
                    }
                }
            }
        }
        person.save flush:true      // to update preferred properties if necessary
        elasticSearchService.index(person)   // had problems with auto-index, so doing it explicitly
    }
}
