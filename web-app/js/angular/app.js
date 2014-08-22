'use strict';

/**
 * Copyright (c) 2014 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
var bendyApp = angular.module('bendyApp', [
        'ui.bootstrap',
        'ngRoute',
        'angularFileUpload',
        'bendyControllers',
        'bendyServices',
        'bendyDirectives'
]);

bendyApp.config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                    when('/contacts', {
                        templateUrl: 'partials/contacts.html',
                        controller: 'BendyContactsCtrl',
                        title: 'Contacts'
                    }).
                    when('/notifications', {
                        templateUrl: 'partials/notifications.html',
//                        controller: 'BendySettingsCtrl',
                        title: 'Notifications'
                    }).
                    when('/groups', {
                        templateUrl: 'partials/groups.html',
//                        controller: 'BendySettingsCtrl',
                        title: 'Groups'
                    }).
                    when('/profile', {
                        templateUrl: 'partials/profile.html',
//                        controller: 'BendySettingsCtrl',
                        title: 'My Profile'
                    }).
                    when('/settings', {
                        templateUrl: 'partials/settings.html',
                        controller: 'BendySettingsCtrl',
                        title: 'Settings'
                    }).
                    otherwise({
                        redirectTo: '/contacts'
                    });
        }
]);
