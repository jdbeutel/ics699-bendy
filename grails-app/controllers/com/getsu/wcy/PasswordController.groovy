package com.getsu.wcy

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.time.TimeCategory

import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = true)
class PasswordController {  // similar to RestfulController

    def authenticationService

    @Transactional
    def update(PasswordCommand cmd) {
        assert authenticationService.isLoggedIn(request) // otherwise the filter would have redirected
        // not using params.id nor params.settings.id/params.user.id because one can edit only one's own User/Settings
        User user = (User) authenticationService.userPrincipal
        // I'm not sure authenticationService would be safe in cmd, so I'm doing this here instead of in a validator.
        if (user.password != authenticationService.encodePassword(cmd.oldPassword)) { // extra authentication
            cmd.errors.rejectValue("oldPassword", "settingsForm.oldPassword.mismatch")
            respond cmd.errors, [status: UNPROCESSABLE_ENTITY, view: 'edit']
            return
        }
        if (!authenticationService.checkPassword(cmd.newPassword)) { // for consistency with signup
            cmd.errors.rejectValue("newPassword", "settingsForm.newPassword.unacceptable", "The new password is unacceptable")
            respond cmd.errors, [status: UNPROCESSABLE_ENTITY, view: 'edit']
            return
        }
        if (!cmd.validate()) {
            respond cmd.errors, [status: UNPROCESSABLE_ENTITY, view: 'edit']
            return
        }
        user.password = authenticationService.encodePassword(cmd.newPassword)
        user.save flush:true, failOnError:true
        respond new PasswordCommand(), [status: OK]     // with updated user version
    }

    // for ajaxvalidate (not restful)
    def validateCurrent(String oldPassword) {
        assert authenticationService.isLoggedIn(request) // otherwise the filter would have redirected
        User user = (User) authenticationService.userPrincipal
        def valid = user.password == authenticationService.encodePassword(oldPassword)
        throttleIfNecessary()
        render([valid: valid] as JSON)
    }

    private static final LIMITER = 'nextValidateCurrentPassword'
    private static final SECONDS = 2
    // guarding against XSS brute-force attack (although I don't know what the front door allows)
    private throttleIfNecessary() {
        def now = new Date()
        def allowed = session[LIMITER]
        if (allowed?.after(now)) {
            sleep((long) allowed.time - now.time)
        }
        session[LIMITER] = TimeCategory.getSeconds(SECONDS) + now
    }
}


class PasswordCommand {
    String oldPassword = ''
    String newPassword = ''
    String newPasswordConfirm = ''

    static constraints = {
        newPassword             password: true, size: 6..40, blank: false, nullable: false
        newPasswordConfirm      password: true, blank: false, nullable: false, validator: {val, obj -> val == obj.newPassword}
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    SettingsCommand() {}    // used by Grails binder
}
