/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */

package com.getsu.wcy

class Settings {

    // static searchable = false        any value makes it searchable

    String dateFormat   // pattern for SimpleDateFormat
    TimeZone timeZone

    static belongsTo = [user:User]

    static constraints = {
    }
}
