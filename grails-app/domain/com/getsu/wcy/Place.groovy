/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package com.getsu.wcy

/**
 * A single location, such as an apartment or office.
 */
class Place {

    static hasMany = [
            addresses:PhysicalAddress, // may be unknown, or separate postal and street addresses
            comLinks:CommunicationLink // for the whole place, not individual connections to it
    ]

    static constraints = {
    }
}