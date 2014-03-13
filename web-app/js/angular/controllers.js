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
