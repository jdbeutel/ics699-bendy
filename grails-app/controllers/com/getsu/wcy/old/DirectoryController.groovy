/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package com.getsu.wcy.old

import com.getsu.wcy.Person

class DirectoryController {

    def index = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        // todo: access controls
        [personInstanceList: Person.list(params), personInstanceTotal: Person.count()]
    }

    def search = {
        render(view: "index", model: index() + [search:params.search])
    }
}
