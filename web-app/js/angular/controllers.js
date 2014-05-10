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

bendyControllers.controller('BendySettingsCtrl', ['$scope', 'Settings', 'Password', '$timeout', 'BendyUtil',
    function ($scope, Settings, Password, $timeout, BendyUtil) {
        $scope.editing = false;
        $scope.changingPassword = false;
        // todo: split the alerts into their own thing.  controller methods via inherited scope?  directives sharing controllers (works with ng-view)?  app-wide service for easy injection?
        $scope.resetAlerts = function() {
            $scope.hasAlerts = false;   // don't clear the array yet, to give the collapse time to animate
            $timeout(function() {
                if (!$scope.hasAlerts) {    // if none were added/replaced during the collapse animation
                    $scope.alerts = [];
                }
            }, 1000);   // 1 second for collapse to finish its animation
        };
        $scope.replaceAlerts = function(type, msg) {
            $scope.alerts = [{type: type, msg: msg}];
            $scope.hasAlerts = true;
        };
        $scope.addAlert = function(type, msg) {
            $scope.alerts.push({type: type, msg: msg});
            $scope.hasAlerts = true;
        };
        $scope.addErrors = function(errors) {
            angular.forEach( errors, function(it) {
                $scope.addAlert('danger', it.message);
            })
        };
        $scope.resetSettingsCommand = function(settingsCommand) {
            $scope.resetAlerts();
            $scope.settingsCommand = settingsCommand;
            $timeout(function() { // after current cycle, so bendyDirty gets updated $modelValue
                $scope.settingsForm.$setPristine();
                $('#settingsForm').trigger('resetvalui');
            });
        };
        $scope.refreshSettings = function() {
            var settings = Settings.get(function() {    // SettingsModel
                $scope.timeZoneOptions = settings.timeZoneOptions;
                $scope.timeZoneOptionsMap = BendyUtil.optionsToMap(settings.timeZoneOptions);
                $scope.dateFormatOptions = settings.dateFormatOptions;
                $scope.dateFormatOptionsMap = BendyUtil.optionsToMap(settings.dateFormatOptions);
                $scope.resetSettingsCommand(settings.settingsCommand);
            });
        };
        $scope.refreshSettings();
        $scope.edit = function() {
            $scope.refreshSettings();
            $scope.editing = true;
        };
        $scope.cancel = function() {
            $scope.editing = false;
            $scope.refreshSettings();
        };
        $scope.update = function(settingsCommand) {
            Settings.update(
                    settingsCommand,
                    function success(updatedSettingsCommand, putResponseHeaders) {
                        $scope.resetSettingsCommand(updatedSettingsCommand);
                        $scope.replaceAlerts('success', 'Settings updated.');
                        $scope.editing = false;
                    },
                    function error(response) {
                        // since it is the same user, forget about optimistic locking and just let the last request win
//                        if (response.status == 409) {   // CONFLICT, optimistic locking exception (with the user herself, as others cannot edit her Settings)
//                            $scope.resetSettingsCommand(response.data); // display the more recent version
//                            $scope.addErrors([{message: 'You updated your Settings in another window, so your changes here were lost.  Please redo them.'}]);
//                        } else {
                            $scope.addErrors(response.data.errors);
//                        }
                    }
            )
        };
        $scope.resetPasswordCommand = function() {
            $scope.resetAlerts();
            $scope.passwordCommand = {oldPassword: '', newPassword: '', newPasswordConfirm: ''};
            $timeout(function() { // after current cycle, so bendyDirty gets updated $modelValue
                $scope.passwordForm.$setPristine();
                $('#passwordForm').trigger('resetvalui');
            });
        };
        $scope.changePassword = function() {
            $scope.resetPasswordCommand();
            $scope.changingPassword = true;
            $timeout(function() { // after current cycle, so angular enables this input first
                $('#oldPassword').focus();
            });
        };
        $scope.cancelPasswordChange = function() {
            $scope.changingPassword = false;
            $scope.resetPasswordCommand();
        };
        $scope.updatePassword = function(passwordCommand) {
            Password.update(
                    passwordCommand,
                    function success(updatedPasswordCommand, putResponseHeaders) {
                        $scope.changingPassword = false;
                        $scope.resetPasswordCommand();
                        $scope.replaceAlerts('success', 'Password updated.');
                    },
                    function error(response) {
                        $scope.addErrors(response.data.errors);
                    }
            )
        };
    }
]);

bendyControllers.controller('BendyAppCtrl', ['$rootScope', '$route', '$scope',
    function($rootScope, $route, $scope) {
        $rootScope.$on('$routeChangeSuccess', function() {
            $rootScope.pageTitle = $route.current.title;
            $scope.navbarCollapse = true;
        })
    }
]);
