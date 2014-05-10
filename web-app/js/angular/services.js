'use strict';

/**
 * Copyright (c) 2014 J. David Beutel <software@getsu.com>
 *
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
var bendyServices = angular.module('bendyServices', ['ngResource']);

bendyServices.factory('Settings', ['$resource',
    function($resource) {
        return $resource('/wcy/settings.json', {}, {
            update: {method:'PUT'}
        });
    }
]);

bendyServices.factory('Password', ['$resource',
    function($resource) {
        return $resource('/wcy/password.json', {}, {
            update: {method:'PUT'}
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
            }
        };
    }
]);
