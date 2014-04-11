package com.getsu.wcy

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.time.TimeCategory

import static org.springframework.http.HttpStatus.CONFLICT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

@Transactional(readOnly = true)
class SettingsController {  // similar to RestfulController

    def authenticationService

    def edit() {
        render view: 'edit'
    }

    def show() {
        assert authenticationService.isLoggedIn(request) // otherwise the filter would have redirected
        // not using params.id because one can edit only one's own Settings
        User user = (User) authenticationService.userPrincipal
        respond new SettingsModel(settingsCommand: new SettingsCommand(user))
    }

    @Transactional
    def update(SettingsCommand cmd) {
        assert authenticationService.isLoggedIn(request) // otherwise the filter would have redirected
        // not using params.id nor params.settings.id/params.user.id because one can edit only one's own User/Settings
        User user = (User) authenticationService.userPrincipal
        if (user.version > cmd.userVersion || user.settings.version > cmd.settingsVersion) {
            respond new SettingsCommand(user), [status: CONFLICT]
            return
        }
        if (cmd.changePassword) {
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
        }
        if (!cmd.validate()) {
            respond cmd.errors, [status: UNPROCESSABLE_ENTITY, view: 'edit']
            return
        }
        user.settings.dateFormat = cmd.dateFormat
        user.settings.timeZone = TimeZone.getTimeZone(cmd.timeZone)
        user.login = cmd.loginEmail
        if (cmd.changePassword) {
            user.password = authenticationService.encodePassword(cmd.newPassword)
        }
        user.save flush:true, failOnError:true
        respond new SettingsCommand(user), [status: OK]     // with updated user version
    }

    // for ajaxvalidate (not restful)
    def validateCurrentPassword(String oldPassword) {
        assert authenticationService.isLoggedIn(request) // otherwise the filter would have redirected
        User user = (User) authenticationService.userPrincipal
        def valid = user.password == authenticationService.encodePassword(oldPassword)
        throttleIfNecessary()
        render([valid: valid] as JSON)
    }

    private static final LIMITER = 'nextValidateCurrentPassword'
    private static final SECONDS = 5
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

class SettingsModel {
    SettingsCommand settingsCommand
    def dateFormatOptions = WcyTagLib.dateFormatOptions()
    def timeZoneOptions = WcyTagLib.timeZoneOptions()
}

class SettingsCommand {
    String loginEmail
    long userVersion

    String dateFormat
    String timeZone
    long settingsVersion

    boolean changePassword = false
    // optional if !changePassword
    String oldPassword = ''
    String newPassword = ''
    String newPasswordConfirm = ''

    static constraints = {
        loginEmail              size: 6..40, email: true, blank: false, nullable: false
        newPassword             password: true, size: 6..40, blank: false, nullable: true, validator: {val, obj -> !obj.changePassword || val}
        newPasswordConfirm      password: true, blank: false, nullable: true, validator: {val, obj -> !obj.changePassword || val == obj.newPassword}
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    SettingsCommand() {}    // used by Grails binder

    SettingsCommand(User user) {
        loginEmail = user.login
        userVersion = user.version

        dateFormat = user.settings.dateFormat
        timeZone = user.settings.timeZone.ID
        settingsVersion = user.settings.version
    }
}
