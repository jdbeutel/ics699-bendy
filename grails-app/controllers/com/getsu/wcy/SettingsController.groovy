package com.getsu.wcy

import grails.transaction.Transactional

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
            if (user.password != authenticationService.encodePassword(cmd.oldPassword)) { // extra authentication
                cmd.errors.rejectValue("oldPassword", "settingsForm.oldPassword.mismatch")
                respond cmd.errors, [status: UNPROCESSABLE_ENTITY, view: 'edit']
                return
            }
            cmd.newPassword = cmd.newPassword?.trim() // todo: see if grails does this automatically
            if (!cmd.newPassword) {
                cmd.errors.rejectValue("newPassword", "settingsForm.newPassword.missing", "Please type in a new password")
                respond cmd.errors, [status: UNPROCESSABLE_ENTITY, view: 'edit']
                return
            }
            if (!(6..40).contains(cmd.newPassword.size())) {
                cmd.errors.rejectValue("newPassword", "settingsForm.newPassword.length", "New password needs from 6 to 40 characters")
                respond cmd.errors, [status: UNPROCESSABLE_ENTITY, view: 'edit']
                return
            }
            if (!authenticationService.checkPassword(cmd.newPassword)) { // for consistency with signup
                cmd.errors.rejectValue("newPassword", "settingsForm.newPassword.unacceptable", "The new password is unacceptable")
                respond cmd.errors, [status: UNPROCESSABLE_ENTITY, view: 'edit']
                return
            }
            if (cmd.newPassword != cmd.newPasswordConfirm) {
                cmd.errors.rejectValue("newPasswordConfirm", "settingsForm.newPasswordConfirm.mismatch", "New password not confirmed.  Please type in your new password again")
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

    // String originalValuesJSON

    static constraints = {
        loginEmail(size:6..40, email:true, blank:false, nullable:false)
        newPassword(password:true, blank:false, nullable: true)
        newPasswordConfirm(password:true, blank:false, nullable:true)
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
