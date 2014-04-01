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

bendyControllers.controller('BendyDirtyFormCtrl', ['$scope', '$timeout',
    function($scope, $timeout) {
        this.makePristine = function() {
            $scope.changed = {};    // not a service because there is one for each form
            $scope.formIsDirty = false;
        };
        this.makePristine();

        this.addDirty = function(name) {
            $scope.changed[name] = true;
            $scope.formIsDirty = true;
        };

        this.removeDirty = function(name) {
            delete $scope.changed[name];
            $scope.formIsDirty = !jQuery.isEmptyObject($scope.changed);
        };

        this.checkPristineForm = function(formCtrl) {
            if (!$scope.formIsDirty) {
                $timeout(function() { // after current cycle, so bendyDirty gets updated $modelValue
                    formCtrl.$setPristine();
                });
            }
        }
    }
]);

bendyControllers.controller('BendySettingsCtrl', ['$scope', 'Settings', '$timeout',
    function ($scope, Settings, $timeout) {
        var settings = Settings.get(function() {    // SettingsModel
            $scope.settingsCommand = settings.settingsCommand;
            $scope.timeZoneOptions = settings.timeZoneOptions;
            $scope.dateFormatOptions = settings.dateFormatOptions;
            $timeout(function() { // after current cycle, so bendyDirty gets updated $modelValue
                $scope.settingsForm.$setPristine();
            });
        });
        $scope.update = function(settingsCommand) {
            Settings.update(
                    settingsCommand,
                    function success(updatedSettingsCommand, putResponseHeaders) {
                        $scope.settingsCommand = updatedSettingsCommand;
                        $timeout(function() { // after current cycle, so bendyDirty gets updated $modelValue
                            $scope.settingsForm.$setPristine();
                        });
                        $scope.errors = [];
                        $scope.message = 'Settings updated.';
                    },
                    function error(response) {
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
        };
        $scope.changePassword = function() {
            $scope.settingsCommand.changePassword = true;
        };
        $scope.cancelPasswordChange = function() {
            var sc = $scope.settingsCommand;
            sc.changePassword = false;
            sc.oldPassword = '';
            sc.newPassword = '';
            sc.newPasswordConfirm = '';
        }
    }
]);
