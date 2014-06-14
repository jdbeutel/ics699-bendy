class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.${format})?"{
            constraints {
                // apply constraints here
            }
        }

        // REST
        "/settings"(resource:'settings', includes:['show', 'edit', 'update'])   // one per user, with implicit id from authentication
        "/password"(resource:'password', includes:['update'])                   // one per user, with implicit id from authentication
        "/people"(resources:'person', includes:['index', 'show', 'save', 'update'])

        "/"(view:"/index")
        "500"(view:'/error')
	}
}
