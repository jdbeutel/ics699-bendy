/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package com.getsu.wcy

import grails.test.spock.IntegrationSpec
import grails.validation.ValidationException
import com.getsu.wcy.Connection.ConnectionType
import com.getsu.wcy.PhoneNumber.PhoneNumberType

class UserIntegrationSpec extends IntegrationSpec {

    def "can save basic User"() {

        given:
        User u = User.createSignupInstance('foo@bar.com')
        u.password = 'my password'

        expect:
        u.save(flush:true)
        u.id
    }

    def "can validate a Person"() {

        given:
        User u = User.createSignupInstance('foo@bar.com')
        u.password = 'my password'

        and: "looking for the blank error"
        u.person.firstGivenName = ''

        expect:
        !u.person.validate()
        u.person.errors.allErrors.collect {it.code}.contains('blank')
        !u.validate(deepValidate:true)

        // Config.groovy's grails.gorm.failOnError = true does not work for cascades,
        // but it does work thanks to User's person validator: { it?.validate() } constraint.
        when:
        u.save()

        then:
        thrown(ValidationException)
    }

    def "can deep validate a Person"() {

        given:
        User u = User.createSignupInstance('foo@bar.com')
        u.password = 'my password'

        expect:
        u.validate() // defaults to deepValidate:true
        !u.errors.allErrors.collect {it.code}.contains('blank')


        when: "looking for the blank error"
        u.person.firstGivenName = ''

        then:
        !u.validate() // defaults to deepValidate:true
        u.errors.allErrors.collect {it.code}.contains('blank')
        !u.person.validate()


        when:
        u.save()

        then:
        thrown(ValidationException)
    }

    def "can validate a Connection"() {

        given:
        User u = User.createSignupInstance('foo@bar.com')
        u.password = 'my password'

        expect:
        u.validate()


        when:
        def connErrors = u.person.connections[0].errors.allErrors

        then:
        !connErrors.collect {it.code}.contains('nullable')


        when:
        u.person.connections[0].type = null

        then:
        !u.validate()


        when:
        connErrors = u.person.connections[0].errors.allErrors

        then:
        connErrors.collect {it.code}.contains('nullable')


        when:
        u.save()

        then:
        thrown(ValidationException)


        when:
        u.person.connections[0].type = ConnectionType.HOME

        then:
        u.validate()
        u.save()
    }

    def "can validate an Address"() {

        given:
        User u = User.createSignupInstance('foo@bar.com')
        u.password = 'my password'

        expect:
        u.person.connections[0].place.addresses[0].city == 'during signup'
        u.validate()
        !u.person.connections[0].place.addresses[0].errors.allErrors.collect {it.code}.contains('blank')

        when:
        u.save()
        u.person.connections[0].place.addresses[0].city = ''

        then:
        !u.validate()


        when:
        def addrErrors = u.person.connections[0].place.addresses[0].errors.allErrors

        then:
        addrErrors.collect {it.code}.contains('blank')


        when:
        u.save()

        then:
        thrown(ValidationException)


        when:
        u.person.connections[0].place.addresses[0].city = 'Honolulu'

        then:
        u.validate()
        u.save()
    }

    def "can validate personal phone"() {

        given:
        User u = User.createSignupInstance('foo@bar.com')
        u.password = 'my password'

        expect:
        u.validate()


        when:
        u.person.addToPhoneNumbers(new PhoneNumber(type:PhoneNumberType.MOBILE))

        then:
        !u.validate()
        u.person.phoneNumbers[0].errors.allErrors.collect {it.code}.contains('nullable')


        when:
        u.save()

        then:
        thrown(ValidationException)


        when:
        u.person.phoneNumbers[0].number = 'x'

        then:
        !u.validate()
        u.person.phoneNumbers[0].errors.allErrors.collect {it.code}.contains('minSize.notmet')


        when:
        u.save()

        then:
        thrown(ValidationException)


        when:
        u.person.phoneNumbers[0].number = '1111'

        then:
        u.validate()
        u.save()
    }

    def "can validate connection phone"() {

        given:
        User u = User.createSignupInstance('foo@bar.com')
        u.password = 'my password'

        expect:
        u.validate()


        when:
        u.person.connections[0].addToPhoneNumbers(new PhoneNumber(type:PhoneNumberType.MOBILE))

        then:
        !u.validate()
        u.person.connections[0].phoneNumbers[0].errors.allErrors.collect {it.code}.contains('nullable')


        when:
        u.save()

        then:
        thrown(ValidationException)


        when:
        u.person.connections[0].phoneNumbers[0].number = 'x'

        then:
        !u.validate()
        u.person.connections[0].phoneNumbers[0].errors.allErrors.collect {it.code}.contains('minSize.notmet')


        when:
        u.save()

        then:
        thrown(ValidationException)


        when:
        u.person.connections[0].phoneNumbers[0].number = '1111'

        then:
        u.validate()
        u.save()
    }

    def "can validate place phone"() {

        given:
        User u = User.createSignupInstance('foo@bar.com')
        u.password = 'my password'

        expect:
        u.validate()


        when:
        u.person.connections[0].place.addToPhoneNumbers(new PhoneNumber(type:PhoneNumberType.MOBILE))

        then:
        !u.validate()
        u.person.connections[0].place.phoneNumbers[0].errors.allErrors.collect {it.code}.contains('nullable')


        when:
        u.save()

        then:
        thrown(ValidationException)


        when:
        u.person.connections[0].place.phoneNumbers[0].number = 'x'

        then:
        !u.validate()
        u.person.connections[0].place.phoneNumbers[0].errors.allErrors.collect {it.code}.contains('minSize.notmet')


        when:
        u.save()

        then:
        thrown(ValidationException)


        when:
        u.person.connections[0].place.phoneNumbers[0].number = '1111'

        then:
        u.validate()
        u.save()
    }
}
