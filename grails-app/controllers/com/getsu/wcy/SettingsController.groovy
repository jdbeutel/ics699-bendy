package com.getsu.wcy

import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.CONFLICT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

@Transactional(readOnly = true)
class SettingsController {  // similar to RestfulController

    def authenticationService

    // used by login.gsp  (kludge?)
    def appIndex() {
        redirect uri: '/'
    }

    def show() {
        assert authenticationService.isLoggedIn(request) // otherwise the filter would have redirected
        // not using params.id because one can edit only one's own Settings
        User user = (User) authenticationService.userPrincipal
        respond new SettingsModel(settingsCommand: new SettingsCommand(user), myPerson: user.person)
    }

    @Transactional
    def update(SettingsCommand cmd) {
        assert authenticationService.isLoggedIn(request) // otherwise the filter would have redirected
        // not using params.id nor params.settings.id/params.user.id because one can edit only one's own User/Settings
        User user = (User) authenticationService.userPrincipal
        // Since users cannot edit each other's data, don't check versions; just let the user's last request stand.
//        if (user.version > cmd.userVersion || user.settings.version > cmd.settingsVersion) {
//            respond new SettingsCommand(user), [status: CONFLICT]
//            return
//        }
        if (!cmd.validate()) {
            respond cmd.errors, [status: UNPROCESSABLE_ENTITY, view: 'edit']
            return
        }
        user.settings.dateFormat = cmd.dateFormat
        user.settings.timeZone = TimeZone.getTimeZone(cmd.timeZone)
        user.login = cmd.loginEmail
        user.save flush:true, failOnError:true
        respond new SettingsCommand(user), [status: OK]     // with updated user version
    }
}

class SettingsModel {
    SettingsCommand settingsCommand
    Person myPerson     // for profile
    def dateFormatOptions = WcyTagLib.dateFormatOptions()
    def timeZoneOptions = WcyTagLib.timeZoneOptions()
}

class SettingsCommand {
    String loginEmail
//    long userVersion

    String dateFormat
    String timeZone
//    long settingsVersion

    static constraints = {
        loginEmail              size: 6..40, email: true, blank: false, nullable: false
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    SettingsCommand() {}    // used by Grails binder

    SettingsCommand(User user) {
        loginEmail = user.login
//        userVersion = user.version

        dateFormat = user.settings.dateFormat
        timeZone = user.settings.timeZone.ID
//        settingsVersion = user.settings.version
    }
}
