/*
 * Copyright (c) 2010 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package com.getsu.wcy

import com.getsu.wcy.old.PersonController
import com.grailsrocks.authentication.SignupForm
import com.grailsrocks.authentication.AuthenticatedUser
import org.springframework.web.multipart.MultipartFile

/**
 * just views delegating to the authentication plugin
 */
class AuthController {

    def authenticationService

    static navigationScope = 'authOptions'

    def index = {
        redirect(action: "login", params: params)
    }

    def login = { }

    def signup(Invitation inv) {
        def alreadySignedUp = User.findByLogin(inv.email)
        [invitation: inv, alreadySignedUp: alreadySignedUp]
    }

    def doSignup(SignupForm sf) {
        assert sf.validate()    // browser-side
        def signupResult = authenticationService.signup( login:sf.invitation.email,
                password:sf.password, email:sf.invitation.email, immediate:true, extraParams:(params + [signupForm: sf]))
        if ((signupResult.result == 0) || (signupResult.result == AuthenticatedUser.AWAITING_CONFIRMATION)) {
            // onSignup event in BootStrap updates and saves the new user's person with params
            if (log.debugEnabled) {
                if (signupResult == AuthenticatedUser.AWAITING_CONFIRMATION) {
                    log.debug("Signup succeeded pending email confirmation for [${sf.login}] / [${sf.email}]")
                } else {
                    log.debug("Signup succeeded for [${sf.login}]")
                }
            }
            redirect(uri: '/') // success
        } else {
            sf.errors.rejectValue("login", "authentication.failure.${signupResult.result}")
            render(view:'signup', model:[personInstance:p, signupForm:sf]) // try again
        }
    }

    def logout = {
        if (authenticationService.isLoggedIn(request) && authenticationService.sessionUser) {
            authenticationService.logout( authenticationService.sessionUser )
        }
    }

    def forgot = { }
}

class SignupForm implements Serializable {
    Invitation invitation
    String password
    String passwordConfirm

    static constraints = {
        password(size:6..40, password:true, blank:false, nullable: false)
        passwordConfirm(password:true, validator: { val, obj -> obj.password == val })
    }
}
