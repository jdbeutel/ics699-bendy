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

bendyControllers.controller('BendyContactsCtrl', ['$scope', 'Person', '$timeout',
    function ($scope, Person, $timeout) {
        $scope.initialPageSize = 10;
        $scope.nextPageSize = 100;
        $scope.sort = 'name';
        $scope.order = 'asc';

        var peopleModel = Person.query(
                {max: $scope.initialPageSize, sort: $scope.sort, order: $scope.order},
                function () {    // PeopleModel
                    $scope.contacts = peopleModel.people;
                    $scope.peopleCount = peopleModel.peopleCount;
                    $scope.phoneNumberTypes = peopleModel.phoneNumberTypes;
                    $scope.connectionTypes = peopleModel.connectionTypes;
                });

        $scope.remainingCount = function() {
            return $scope.contacts ? $scope.peopleCount - $scope.contacts.length : 0;
        };

        $scope.nextIsFinalPage = function() {
             // To finish off the results, the server will go up to 30% over max.
            return $scope.remainingCount() <= $scope.nextPageSize * 1.30;
        };

        $scope.loadNextPage = function() {
            $scope.loadMoreContacts($scope.nextPageSize);
        };

        $scope.loadMoreContacts = function(max) {
            peopleModel = Person.query(
                    {
                        offset: $scope.contacts.length,
                        q: $scope.searchTerms,
                        max: max,
                        sort: $scope.sort,
                        order: $scope.order
                    },
                    function () {    // PeopleModel
                        Array.prototype.push.apply($scope.contacts, peopleModel.people);
                        $scope.peopleCount = peopleModel.peopleCount;   // probably hasn't changed, but anyway
                    });
        };

        function reload() {
            $scope.contacts.length = 0;     // clear list
            $scope.peopleCount = 0;
            $scope.loadMoreContacts($scope.initialPageSize);
        }

        this.sortBy = function(property) {
            if (property == $scope.sort) {
                $scope.order = ($scope.order == 'asc' ? 'desc' : 'asc');
            } else {
                $scope.sort = property;
                $scope.order = 'asc';
            }
            reload();
        };

        $scope.search = function(searchTerms) {
            $scope.searchTerms = searchTerms;
            reload();
            $timeout(function() { // after current cycle, so bendyDirty gets updated $modelValue
                $scope.searchContactsForm.$setPristine();
            });
        };

        $scope.collapse = function(person) {
            person.isCollapsed = true;  // hack because I can't get the BendyPersonCtrl (scope) over both table rows
        };

        $scope.expand = function(person) {
            person.isCollapsed = false;
        };
    }
]);

bendyControllers.controller('BendyPersonCtrl', ['$scope', 'Person', '$upload', 'datepickerConfig', '$filter', 'BendyUtil', '$timeout',
    function ($scope, Person, $upload, datepickerConfig, $filter, BendyUtil, $timeout) {
        $scope.editingPerson = null;
        $scope.addChoices = [];
        $scope.addChoicesDropdownStatus = {isOpen: false};  // hack: doesn't work without this indirection
        $scope.photoNoCache = '';
        datepickerConfig.showWeeks = false;

        $scope.edit = function() {
            $scope.editingPerson = angular.copy($scope.person);  // inherited from ng-repeat
            // avoid sending derivative properties, which cannot be edited directly and may override edits
            delete $scope.editingPerson.preferredEmail;
            delete $scope.editingPerson.preferredPhone;
            delete $scope.editingPerson.preferredConnection;
            $scope.addingBirthDate = false;
            $scope.addingPhoto = false;
            $scope.refreshAddChoices();
            $timeout(function() { // after current cycle, so bendyDirty gets updated $modelValue
                $scope.personForm.$setPristine();
                //$('#settingsForm').trigger('resetvalui');     todo: get relative element somehow for this
            });
        };

        $scope.cancel = function() {
            $scope.editingPerson = null;
        };

        $scope.update = function() {
            $scope.copyNameProperties($scope.person, $scope.editingPerson);
            angular.copy($scope.person.connections, $scope.editingPerson.connections);  // preserve nested updates by child forms
            // Avoid submitting local time part of date, because the server may be in a different time zone.
            $scope.editingPerson.birthDate = $filter('date')($scope.editingPerson.birthDate, 'yyyy-MM-dd');
            Person.update(
                    {id: $scope.editingPerson.id},
                    $scope.editingPerson,
                    function success(updatedPerson, putResponseHeaders) {
                        updatedPerson.isCollapsed = $scope.person.isCollapsed; // preserve hack
                        $scope.setPerson(updatedPerson);
                        $scope.editingPerson = null;
                        // todo: positive feedback
                    },
                    function error(response) {
                        alert(response.data.errors);    // todo: $scope.addErrors(response.data.errors);
                    }
            )
        };

        $scope.copyNameProperties = function(src, dest) {
            var nameProps = ['preferredName', 'honorific', 'firstGivenName', 'middleGivenNames', 'familyName', 'suffix'];
            angular.forEach(nameProps, function(value) {
                dest[value] = src[value];
            })
        };

        $scope.setPerson = function(person) {
            angular.copy(person, $scope.person); // allows update by child scope
        };

        $scope.onFileSelect = function($files) {
            $scope.upload = $upload.upload({
                url: '/person/uploadPhoto/' + $scope.editingPerson.id,
                file: $files[0]
            }).progress(function(evt) {
                console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
            }).success(function(data, status, headers, config) {
                console.log(data);
                $scope.photoNoCache = '?photoNoCache=' + new Date().getTime();    // force img reload
                $scope.person.photo = {uploaded: true};     // trigger ng-show of first upload
            })
        };

        $scope.openBirthDatePicker = function($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.birthDatePickerOpened = true;
        };

        $scope.refreshAddChoices = function() {
            if (!$scope.editingPerson) {
                $scope.addChoices = [];
            } else {
                $scope.addChoices = ['Email', 'Phone'];
                if (!$scope.editingPerson.birthDate && !$scope.addingBirthDate) {
                    $scope.addChoices.push('Birth Date')
                }
                if (!$scope.editingPerson.photo && !$scope.addingPhoto) {
                    $scope.addChoices.push('Photo')
                }
            }
        };

        $scope.addField = function(choice) {
            if (choice == 'Email') {
                var addedEmail = {
                    name: $scope.editingPerson.name,    // todo: non-redundant name UI
                    address: '',
                    level: 'PERSONAL',
                    connectionType: null
                };
                $scope.editingPerson.emailAddresses.push(addedEmail);
            }
            if (choice == 'Phone') {
                var addedPhone = {
                    type: 'MOBILE',
                    number: '',
                    level: 'PERSONAL',
                    connectionType: null
                };
                $scope.editingPerson.phoneNumbers.push(addedPhone);
            }
            if (choice == 'Birth Date') {
                $scope.addingBirthDate = true;
                BendyUtil.removeFromArray('Birth Date', $scope.addChoices);
            }
            if (choice == 'Photo') {
                $scope.addingPhoto = true;
                BendyUtil.removeFromArray('Photo', $scope.addChoices);
            }
            $scope.addChoicesDropdownStatus.isOpen = false;
        };
    }
]);

bendyControllers.controller('BendyPersonNameCtrl', ['$scope', 'Person', '$timeout',
    function ($scope, Person, $timeout) {
        $scope.editingName = null;
        $scope.isNameCollapsed = true;

        $scope.collapseName = function() {
            $scope.isNameCollapsed = true;
        };

        $scope.expandName = function() {
            $scope.isNameCollapsed = false;
        };

        $scope.editName = function() {
            $scope.editingName = angular.copy($scope.person);  // inherited from ng-repeat via BendyPersonCtrl
            $timeout(function() { // after current cycle, so bendyDirty gets updated $modelValue
                $scope.nameForm.$setPristine();
                //$('#settingsForm').trigger('resetvalui');     todo: get relative element somehow for this
            });
        };

        $scope.updateName = function() {
            var freshName = angular.copy($scope.person);  // inherited from ng-repeat via BendyPersonCtrl
            $scope.copyNameProperties($scope.editingName, freshName);
            Person.update(
                    {id: freshName.id},
                    freshName,
                    function success(updatedPerson, putResponseHeaders) {
                        $scope.setPerson(updatedPerson);
                        $scope.editingName = null;
                        // todo: positive feedback
                    },
                    function error(response) {
                        alert(response.data.errors);    // todo: $scope.addErrors(response.data.errors);
                    }
            )
        };

        $scope.cancelEditName = function() {
            $scope.editingName = null;
        };
    }
]);

bendyControllers.controller('BendyProfileCtrl', ['$scope', 'Settings',
    function ($scope, Settings) {
        $scope.myProfile = true;
        $scope.person = {};
        var settings = Settings.get(function() {    // SettingsModel
            $scope.person = settings.myPerson;
        });
    }
]);

bendyControllers.controller('BendyConnectionsCtrl', ['$scope',
    function ($scope) {

        $scope.collapse = function(connection) {
            connection.isCollapsed = true;  // like BendyPersonCtrl
        };

        $scope.expand = function(connection) {
            connection.isCollapsed = false;
        };
    }
]);

bendyControllers.controller('BendyConnectionCtrl', ['$scope', 'Connection', '$timeout', 'Person',
    function ($scope, Connection, $timeout, Person) {
        $scope.editingConnection = null;
        $scope.addChoices = ['Direct Email', 'Direct Phone', 'Address', 'Email', 'Phone'];
        $scope.addChoicesDropdownStatus = {isOpen: false};  // hack: doesn't work without this indirection

        $scope.edit = function() {
            $scope.editingConnection = angular.copy($scope.connection);  // inherited from ng-repeat
            // avoid sending derivative properties, which cannot be edited directly and may override edits
            delete $scope.editingConnection.preferredEmail;
            delete $scope.editingConnection.preferredPhone;
            delete $scope.editingConnection.preferredAddress;
            $timeout(function() { // after current cycle, so bendyDirty gets updated $modelValue
                $scope.connectionForm.$setPristine();
                //$('#settingsForm').trigger('resetvalui');     todo: get relative element somehow for this
            });
        };

        $scope.cancel = function() {
            $scope.editingConnection = null;
        };

        $scope.update = function() {
            Connection.update(
                    {id: $scope.editingConnection.id},
                    $scope.editingConnection,
                    function success(updatedConnection, putResponseHeaders) {
                        updatedConnection.isCollapsed = $scope.connection.isCollapsed; // preserve hack
                        $scope.setConnection(updatedConnection);
                        $scope.editingConnection = null;
                        // todo: positive feedback
                        Person.get(     // update preferred properties on read-only view if necessary
                                {id: $scope.person.id},
                                function success(updatedPerson, putResponseHeaders) {
                                    updatedPerson.isCollapsed = $scope.person.isCollapsed; // preserve hack
                                    $scope.setPerson(updatedPerson);
                                },
                                function error(response) {
                                    alert(response.data.errors);    // todo: $scope.addErrors(response.data.errors);
                                }
                        )
                    },
                    function error(response) {
                        alert(response.data.errors);    // todo: $scope.addErrors(response.data.errors);
                    }
            )
        };

        $scope.setConnection = function(connection) {
            angular.copy(connection, $scope.connection); // allows update by child scope
        };

        $scope.addField = function(choice) {
            if (choice == 'Direct Email') {
                var addedDirectEmail = {
                    name: $scope.person.name,    // todo: non-redundant name UI
                    address: '',
                    level: 'DIRECT',
                    connectionType: $scope.editingConnection.type
                };
                $scope.editingConnection.emailAddresses.push(addedDirectEmail);
            }
            if (choice == 'Email') {
                var addedEmail = {
                    name: $scope.person.name,    // todo: non-redundant name UI
                    address: '',
                    level: 'GENERAL',
                    connectionType: $scope.editingConnection.type
                };
                $scope.editingConnection.place.emailAddresses.push(addedEmail);
            }
            if (choice == 'Direct Phone') {
                var addedDirectPhone = {
                    type: 'MOBILE',
                    number: '',
                    level: 'DIRECT',
                    connectionType: $scope.editingConnection.type
                };
                $scope.editingConnection.phoneNumbers.push(addedDirectPhone);
            }
            if (choice == 'Phone') {
                var addedPhone = {
                    type: 'LANDLINE',
                    number: '',
                    level: 'GENERAL',
                    connectionType: $scope.editingConnection.type
                };
                $scope.editingConnection.place.phoneNumbers.push(addedPhone);
            }
            $scope.addChoicesDropdownStatus.isOpen = false;
        };
    }
]);
