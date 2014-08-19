/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package com.getsu.wcy

import com.getsu.wcy.Connection.ConnectionType

class Person {

    static searchable = {
        connections component: true
        phoneNumbers component: true
        emailAddresses component: true
        instantMessengerAddresses component: true
        skypeNames component: true
        twitterNames component: true
        preferredEmail component: true
        preferredPhone component: true
        preferredConnection component: true
        photo component: true

        name boost: 4.0, index: 'not_analyzed'

        preferredName   index: 'not_analyzed'
        honorific   index: 'not_analyzed'
        firstGivenName boost: 3.0, index: 'not_analyzed'
        middleGivenNames boost: 2.0, index: 'not_analyzed'
        familyName   index: 'not_analyzed'
        suffix   index: 'not_analyzed'
    }

    String preferredName
    String honorific // e.g. Mr., Dr.
    String firstGivenName
    String middleGivenNames
    String familyName // last names in English
    String suffix // e.g. Jr., III, Sr., M.D., Ph.D.

    Photo photo
    Date birthDate

    List<Connection> connections // to Places

    // personal communication links (separately and statically typed for DomainBuilder at least)
    List<PhoneNumber> phoneNumbers // e.g., mobile phones
    List<EmailAddress> emailAddresses
    List<InstantMessengerAddress> instantMessengerAddresses
    List<SkypeName> skypeNames
    List<TwitterName> twitterNames
    // etc...

    EmailAddress preferredEmail     // de-normalized
    PhoneNumber preferredPhone      // de-normalized
    Connection preferredConnection  // de-normalized

    static hasMany = CommunicationLinks.hasMany + [
            connections:Connection,
    ]

    static constraints = {
        preferredName nullable:true
        honorific nullable:true
        firstGivenName blank:false
        middleGivenNames nullable:true
        familyName blank:false
        suffix nullable:true
        photo nullable:true
        birthDate nullable:true

        preferredEmail nullable:true
        preferredPhone nullable:true
        preferredConnection nullable:true

        // Connection's belongsTo = Person handles validation, but generating errors one level too high, so
        connections validator: { it?.every { it?.validate() } }  // work-around to get errors on specific connections

        CommunicationLinks.constraints(delegate)
    }

    static mapping = {
        sort name:'asc'
        // connections handled by belongsTo = Person in Connection
        CommunicationLinks.mapping(delegate)
    }

    // generated property, persisted for GORM sorting by <g:sortableColumn>
    @Deprecated void setName(String ignored) {}
    String getName() { "${preferredName ?: firstGivenName} ${familyName}" }

    // for sortableColumn & JSON (todo: make updates of associates trigger update of Person)
    EmailAddress getPreferredEmail() {
        // todo: user preferences and smarter selection by ConnectionType
        def connectionWithEmail = connections.find {it.emailAddresses}
        def connectionWithPlaceEmail = connections.find {it.place.emailAddresses}
        [this, connectionWithEmail, connectionWithPlaceEmail].findResult {it?.emailAddresses?.getAt(0)}
    }

    // for sortableColumn & JSON (todo: make updates of associates trigger update of Person)
    PhoneNumber getPreferredPhone() {
        // todo: user preferences and smarter selection by PhoneNumberType and ConnectionType
        def phoneNumbersOfConnection = connections.findResult {it.phoneNumbers}
        def phoneNumbersOfPlace = connections.findResult {it.place.phoneNumbers}
        [phoneNumbers, phoneNumbersOfConnection, phoneNumbersOfPlace].findResult {it?.getAt(0)}
    }

    // for sortableColumn & JSON (todo: make updates of associates trigger update of Person)
    Connection getPreferredConnection() {
        // todo: user preferences and smarter selection by ConnectionType and streetType/postalType
        def home = connections.find {it.type == ConnectionType.HOME && it.place?.addresses}
        def work = connections.find {it.type == ConnectionType.WORK && it.place?.addresses}
        home ?: work
    }
}
