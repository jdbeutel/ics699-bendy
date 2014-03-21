'use strict';

/**
 * Copyright (c) 2014 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
var bendyControllers = angular.module('bendyControllers', []);

bendyControllers.controller('BendyChangeCtrl', ['$scope',
    function($scope) {
        $scope.changed = {};
        $scope.dirty = false;

        this.addChanged = function(name) {
            $scope.changed[name] = true;
            $scope.dirty = true;
            $scope.$apply();    // required for call from event handler
        };

        this.removeChanged = function(name) {
            delete $scope.changed[name];
            $scope.dirty = !jQuery.isEmptyObject($scope.changed);
            $scope.$apply();    // required for call from event handler
        }
    }
]);

bendyControllers.controller('BendySettingsCtrl', ['$scope', 'Settings', '$log',
    function ($scope, Settings, $log) {
        var settings = Settings.get(function() {    // SettingsModel
            $scope.settingsCommand = settings.settingsCommand;
            $scope.timeZoneOptions = settings.timeZoneOptions;
            $scope.dateFormatOptions = settings.dateFormatOptions;
        });
        $scope.update = function(settingsCommand) {
            Settings.update(
                    settingsCommand,
                    function(settingsCommand, putResponseHeaders) { // success
                        $log.log('got success response');
                        $log.log(settingsCommand);
                        $scope.settingsCommand = settingsCommand;
                        $scope.settingsForm.$setPristine();
                        $scope.errors = [];
                        $scope.message = 'Settings updated.';
                    },
                    function(response) { // error
                        $log.log('got error response ' + response.status);
                        $log.log(response.data);
                        $scope.message = '';
                        if (response.status == 409) {   // CONFLICT, optimistic locking exception (with the user herself, as others cannot edit her Settings)
                            $scope.errors = [{message: 'You updated your Settings in another window, so your changes here were lost.  Please redo them.'}];
                            // todo: since it is the same user, forget about optimistic locking and just let the last one win?
                            $scope.settingsCommand = response.data; // display updated
                            $scope.settingsForm.$setPristine();
                        } else {
                            $scope.errors = response.data.errors;
                        }
                    }
            )
        }
    }
]);
