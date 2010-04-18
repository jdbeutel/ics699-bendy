/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package com.getsu.wcy

import java.text.SimpleDateFormat

class User {

    // required by authentication plugin
    String login // we use an email address here
    String password // MD5 hash hex
    String email // unused and not persisted
    int status

    Person person
    Settings settings

    static constraints = {
        login blank:false, email:true, unique:true
        password blank:false
    }

    static transients = ['email']

    static User createInstance(String loginID) {
        def p = new Person(givenNames:'John', familyName:'Doe')
        def s = new Settings( dateFormat:new SimpleDateFormat('yyyy-MM-dd HH:mm'), timeZone:TimeZone.default )
        return new User(login:loginID, person:p, settings:s)
    }

    def beforeInsert() { // GORM event hook
        // todo: withNewSession?
        person.save(failOnError:true) // special handling because not all Person belongsTo User
    }
}
