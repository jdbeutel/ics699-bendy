/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package com.getsu.wcy

class InstantMessengerAddress {

    static searchable = {
        network   index: 'not_analyzed'
        name   index: 'not_analyzed'
    }

    String network // e.g. AIM
    String name

    static constraints = {
    }
}
