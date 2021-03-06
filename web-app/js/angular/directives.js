'use strict';

/**
 * Copyright (c) 2014 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
var bendyDirectives = angular.module('bendyDirectives', []);

bendyDirectives.directive('bendyChangeChecker', function() {
    return {
        require: '^ngController',   // the enclosing BendyChangeCtrl
        scope: {},
        link: function postLink(scope, element, attrs, bendyChangeCtrl) {
            scope.originalValue = element.val();
            element.on('change keydown', function(event) {
                scope.changed = element.val() != scope.originalValue;
                if (scope.changed) {
                    bendyChangeCtrl.addChanged(attrs.name);
                    element.addClass('changed');
                } else {
                    bendyChangeCtrl.removeChanged(attrs.name);
                    element.removeClass('changed');
                }
            });
        }
    }
});

bendyDirectives.directive('bendyDirty', [function() {
    return {
        priority: 210,  // postLink after the select & option directives
        require: ['bendyDirty', 'ngModel', '^form', '^ngController'],
        scope: {
            fieldName: '@name'
        },
        controller: function($scope, $element, $attrs, $transclude) {

            this.linkCtrls = function(modelCtrl, formCtrl, bendyDirtyFormCtrl) {
                $scope.modelCtrl = modelCtrl;
                $scope.formCtrl = formCtrl;
                $scope.bendyDirtyFormCtrl = bendyDirtyFormCtrl;
            };

            this.$setPristine = function() {    // called by formCtrl
                $scope.originalModelValue = $scope.modelCtrl.$modelValue;
                $scope.bendyDirtyFormCtrl.removeDirty($scope.fieldName);
                // This function was called because form is $pristine, so don't checkPristineForm here.
            };

            this.checkPristine = function(value) {
                var orig = $scope.originalModelValue;
                if (($scope.modelCtrl.$isEmpty(value) && $scope.modelCtrl.$isEmpty(orig)) || value == orig) {
                    $scope.modelCtrl.$setPristine();
                    $scope.bendyDirtyFormCtrl.removeDirty($scope.fieldName);
                    $scope.bendyDirtyFormCtrl.checkPristineForm($scope.formCtrl);
                } else {
                    $scope.bendyDirtyFormCtrl.addDirty($scope.fieldName);
                }
            }
        },
        link: function postLink(scope, element, attrs, ctrls) {

            var bendyDirty = ctrls[0],
                    modelCtrl = ctrls[1],
                    formCtrl = ctrls[2],
                    bendyDirtyFormCtrl = ctrls[3];

            bendyDirty.linkCtrls(modelCtrl, formCtrl, bendyDirtyFormCtrl);

            formCtrl.$addControl(bendyDirty);   // just $setPristine() callback

            scope.$on('$destroy', function() {
                formCtrl.$removeControl(bendyDirty);
            });

            // called last in $parsers chain [0..-1], to spy on what modelCtrl.$setViewValue() will assign to modelCtrl.$modelValue,
            // after change to dirty (so we can change back to pristine here)
            modelCtrl.$parsers.push(function (value) {
                bendyDirty.checkPristine(value);
                return value;
            });

            // called first in $formatters chain [-1..0], to spy on modelCtrl.$modelValue before any other formatters
            modelCtrl.$formatters.push(function (value) {
                bendyDirty.checkPristine(value);
                return value;
            });
        }
    }
}]);

// Strangely, I couldn't find some built-in way to do this.
bendyDirectives.directive('bendyAlertIcon', function() {
    return {
        restrict: 'E',
        scope: {
            type: '@'   // duplicate attr because scope of parent alert directive is inaccessible due to transclude:true
        },
        controller: function($scope) {
            var icons = {
                success: 'glyphicon-ok-sign',
                info: 'glyphicon-info-sign',
                warning: 'glyphicon-warning-sign',
                danger: 'glyphicon-exclamation-sign'
            };
            $scope.iconClass = icons[$scope.type] || icons.warning;
        },
        template: '<i class="glyphicon {{iconClass}}"></i>',
        replace: true
    }
});

bendyDirectives.directive('bendyTypeIcon', function() {
    return {
        restrict: 'E',
        scope: {
            kind: '@',
            type: '='
        },
        controller: function($scope) {
            var iconMaps = {
                connection: {
                    HOME: 'glyphicon-home',
                    WORK: 'glyphicon-briefcase',
                    SCHOOL: 'glyphicon-book'
                },
                phone: {
                    LANDLINE: 'glyphicon-phone-alt',
                    MOBILE: 'glyphicon-phone',
                    FAX: 'glyphicon-print'
                },
                order: {
                    asc: 'glyphicon-sort-by-alphabet',
                    desc: 'glyphicon-sort-by-alphabet-alt'
                }
            };
            var icons = iconMaps[$scope.kind] || {};
            $scope.getIconClass = function() {      // to re-evaluate when parent $scope.type changes
                return icons[$scope.type] || null;
            }
        },
        template: '<span ng-if="getIconClass()" class="glyphicon {{getIconClass()}}" title="{{type}}"><span class="sr-only">{{type}}</span></span>',
        replace: true
    }
});

bendyDirectives.directive('bendySortButton', function () {
    return {
        restrict: 'E',
        require: ['bendySortButton', '^ngController'],
        scope: {
            propertyName: '@',
            sort: '=',
            order: '='
        },
        controller: function ($scope) {
            function sortingOnThisProperty() {
                return $scope.sort == $scope.propertyName;
            }
            $scope.selectedClass = function () {
                return {'btn-info': sortingOnThisProperty()}
            };
            $scope.selectedOrder = function () {
                return sortingOnThisProperty() ? $scope.order : 'asc';
            };
            this.linkCtrl = function (bendyContactsCtrl) {
                $scope.bendyContactsCtrl = bendyContactsCtrl;
            };
            $scope.doSort = function () {
                $scope.bendyContactsCtrl.sortBy($scope.propertyName);
            }
        },
        link: function postLink(scope, element, attrs, ctrls) {

            var sortButton = ctrls[0],
                    bendyContactsCtrl = ctrls[1];

            sortButton.linkCtrl(bendyContactsCtrl);
        },
        template: '<button type="button" class="btn btn-default btn-xs" ng-class="selectedClass()" ng-click="doSort()">'
                + '    <bendy-type-icon kind="order" type="selectedOrder()"></bendy-type-icon>'
                + '</button>',
        replace: true
    }
});
