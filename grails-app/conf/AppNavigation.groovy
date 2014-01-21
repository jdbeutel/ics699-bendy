/**
 * Navigation DSL for platform-core plugin.
 *
 * Created by J. David Beutel on 2014-01-20.
 */

def loggedIn = { authenticationService.isLoggedIn(request) }

navigation = {
    app {
        contacts        order: 10, controller: 'contact', action: 'index'
        groups          order: 20, controller: 'group', action: 'index'
        myProfile       order: 30, controller: 'person', action: 'editMyProfile'
        notifications   order: 40, controller: 'notification', action: 'index'
        directory       order: 50, action: 'index'
        settings        order: 60, action: 'index'
    }

    authOptions {
        logout order:99, controller: 'auth', visible: loggedIn
    }
}
