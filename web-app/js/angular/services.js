'use strict';

/**
 * Copyright (c) 2014 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
var bendyServices = angular.module('bendyServices', ['ngResource']);

bendyServices.factory('Settings', ['$resource',
    function($resource) {
        return $resource('/settings.json', {}, {
            update: {method:'PUT'}
        });
    }
]);

bendyServices.factory('Password', ['$resource',
    function($resource) {
        return $resource('/password.json', {}, {
            update: {method:'PUT'}
        });
    }
]);

bendyServices.factory('Person', ['$resource',
    function($resource) {
        return $resource('/people/:id.json', {}, {
            update: {method:'PUT'},
            query: {method:'GET', isArray:false}
        });
    }
]);

bendyServices.factory('Connection', ['$resource',
    function($resource) {
        return $resource('/connections/:id.json', {}, {
            update: {method:'PUT'},
            query: {method:'GET', isArray:false}
        });
    }
]);

bendyServices.factory('Address', ['$resource',
    function($resource) {
        return $resource('/addresses/:id.json', {}, {
            update: {method:'PUT'},
            query: {method:'GET', isArray:false}
        });
    }
]);

bendyServices.factory('BendyUtil', [
    function() {
        return {
            optionsToMap: function(options) {
                var map = {};
                angular.forEach(options, function(element) {
                    map[element.key] = element.value;
                });
                return map;
            },
            removeFromArray: function(value, array) {
                var index = jQuery.inArray(value, array);
                if (index != -1) {
                    array.splice(index, 1);
                }
            }
        };
    }
]);
