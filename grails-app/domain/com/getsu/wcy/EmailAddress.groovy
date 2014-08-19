/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package com.getsu.wcy

class EmailAddress {

    static searchable = {
        name       index: 'not_analyzed'
        address    index: 'not_analyzed'
        level      index: 'not_analyzed'
        connectionType  index: 'not_analyzed'
    }

    String name
    String address

    CommunicationLinks.Level level              // de-normalized for preferredEmail
    Connection.ConnectionType connectionType    // de-normalized for preferredEmail

    static constraints = {
        address email:true
        connectionType nullable:true
    }
}
