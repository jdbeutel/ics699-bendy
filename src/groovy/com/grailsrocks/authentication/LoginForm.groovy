package com.grailsrocks.authentication

class LoginForm implements Serializable {
	String login
	String password
	
	boolean rememberMe

    // jbeutel overrode plugin to remove size constraint; they either match or they don't
	static constraints = {
		login(nullable: false, blank:false)
		password(password:true, nullable: false, blank:false)
	}
}
