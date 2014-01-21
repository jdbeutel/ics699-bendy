/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package com.getsu.wcy

import spock.lang.Specification

class UniqueSpec extends Specification {

    def "uncomparable equals with the same hash code are not unique"() {

        given:
        def a = new Uncomparable(id:'foo')
        def b = new Uncomparable(id:'foo')

        expect:
        a == b
        [a, b].unique() == [a]
    }

    // http://jira.codehaus.org/browse/GROOVY-4101 has been fixed
    def "uncomparable unequals with the same hash code are unique"() {

        given:
        def a = new Uncomparable(id:'0-42L')
        def b = new Uncomparable(id:'0-43-')

        expect:
        a != b
        a.hashCode() == b.hashCode()
        [a, b].unique() == [a, b]
    }

    def "uncomparable unequal timezones with different IDs but the same hashcode are unique"() {

        given:
        def a = TimeZone.getTimeZone("US/Hawaii")
        def b = TimeZone.getTimeZone("Pacific/Honolulu")

        expect:
        a != b
        a.hashCode() == b.hashCode()
        [a, b].unique() == [a, b]
    }
}

class Uncomparable {
    String id
    boolean equals(Object that) { that && that instanceof Uncomparable && that.id == id }
    int hashCode() { id.hashCode() }
    String toString() { id }
}
