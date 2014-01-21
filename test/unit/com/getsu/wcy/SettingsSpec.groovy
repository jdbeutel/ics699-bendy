/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */

package com.getsu.wcy

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(Settings)
class SettingsSpec extends Specification {

    def "can instantiate Settings"() {

        expect:
        new Settings()
    }
}
