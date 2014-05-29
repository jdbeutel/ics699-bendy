/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package com.getsu.wcy

class PhoneNumber {

    String number
    PhoneNumberType type

    CommunicationLinks.Level level              // de-normalized for preferredPhone
    Connection.ConnectionType connectionType    // de-normalized for preferredPhone

    static constraints = {
        number minSize:3
        connectionType nullable:true
    }

    enum PhoneNumberType {
        LANDLINE, MOBILE, FAX
    }
}
