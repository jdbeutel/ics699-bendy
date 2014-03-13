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
