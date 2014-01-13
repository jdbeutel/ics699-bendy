/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */

package com.getsu.wcy

class Settings {

    String dateFormat   // pattern for SimpleDateFormat
    TimeZone timeZone

    static belongsTo = [user:User]

    static constraints = {
    }
}
