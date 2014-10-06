package com.getsu.wcy

class Invitation {

    String email
    Person person

    static constraints = {
    }

    static mapping = {
        id generator: 'assigned'    // for demo; will be random ticket for security
    }
}
