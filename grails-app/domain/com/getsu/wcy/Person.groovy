/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package com.getsu.wcy

import com.getsu.wcy.Connection.ConnectionType

class Person {

    String preferredName
    String honorific // e.g. Mr., Dr.
    String firstGivenName
    String middleGivenNames
    String familyName // last names in English
    String suffix // e.g. Jr., III, Sr., M.D., Ph.D.

    byte[] photo
    String photoFileName // keep for format clues in the file name extension?
    Date birthDate

    List<Connection> connections // to Places

    // personal communication links (separately and statically typed for DomainBuilder at least)
    List<PhoneNumber> phoneNumbers // e.g., mobile phones
    List<EmailAddress> emailAddresses
    List<InstantMessengerAddress> instantMessengerAddresses
    List<SkypeName> skypeNames
    List<TwitterName> twitterNames
    // etc...

    String originalValuesJSON

    static transients = ['originalValuesJSON', 'preferredPhone', 'preferredConnection']

    static hasMany = [
            connections:Connection,
            phoneNumbers:PhoneNumber,
            emailAddresses:EmailAddress,
            instantMessengerAddresses:InstantMessengerAddress,
            skypeNames:SkypeName,
            twitterNames:TwitterName,
    ]

    static constraints = {
        preferredName nullable:true
        honorific nullable:true
        firstGivenName blank:false
        middleGivenNames nullable:true
        familyName blank:false
        suffix nullable:true
        photo nullable:true
        photoFileName nullable:true
        birthDate nullable:true
    }

    static mapping = {
        sort name:'asc'
        // connections handled by belongsTo = Person in Connection
        phoneNumbers cascade:'persist,merge,save-update'
        emailAddresses cascade:'persist,merge,save-update'
        instantMessengerAddresses cascade:'persist,merge,save-update'
        skypeNames cascade:'persist,merge,save-update'
        twitterNames cascade:'persist,merge,save-update'
    }

    // generated property, persisted for GORM sorting by <g:sortableColumn>
    @Deprecated void setName(String ignored) {}
    String getName() { "${preferredName ?: firstGivenName} ${familyName}" }

    // I doubt GORM can persist this (for sortableColumn), because updates of associates
    // would need to cascade up to trigger updates of Person.
    def getPreferredPhone() {
        // todo: user preferences and smarter selection by PhoneNumberType and ConnectionType
        def connectionWithPhoneNumber = connections.find {it.phoneNumbers}
        def connectionWithPlacePhoneNumber = connections.find {it.place.phoneNumbers}
        if (phoneNumbers[0]) {
            return [type:phoneNumbers[0].type, number:phoneNumbers[0].number]
        } else if (connectionWithPhoneNumber) {
            def c = connectionWithPhoneNumber
            return [type:"$c.type ${c.phoneNumbers[0].type}", number:c.phoneNumbers[0].number]
        } else if (connectionWithPlacePhoneNumber) {
            def c = connectionWithPlacePhoneNumber
            return [type:"$c.type ${c.place.phoneNumbers[0].type}", number:c.place.phoneNumbers[0].number]
        }
    }

    // I doubt GORM can persist this (for sortableColumn), because updates of associates
    // would need to cascade up to trigger updates of Person.
    Connection getPreferredConnection() {
        // todo: user preferences and smarter selection by ConnectionType and streetType/postalType
        def home = connections.find {it.type == ConnectionType.HOME && it.place?.addresses}
        def work = connections.find {it.type == ConnectionType.WORK && it.place?.addresses}
        return home ?: work
    }
}
