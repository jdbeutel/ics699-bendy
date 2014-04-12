%{--
- Copyright (c) 2010 J. David Beutel <software@getsu.com>
-
- Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
--}%
<%@ page import="com.getsu.wcy.Settings" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <r:require module="angular"/>
    <title>Edit Settings</title>
    <script src="${resource(dir:'js/lib/js-webshim-1.12.5/dev/extras', file:'modernizr-custom.js')}" ></script>
    <script src="${resource(dir:'js/lib/js-webshim-1.12.5/dev', file:'polyfiller.js')}" ></script>
    <script>
        webshims.setOptions('forms', {
                    lazyCustomMessages: true,
                    replaceValidationUI: true,
                    addValidators: true,
                    iVal: {
                        sel: '.ws-validate',
                        handleBubble: false,
                        recheckDelay: 5000,  // after 5 second pause, or immediately on blur
                        submitCheck: true,

                        // bootstrap specific classes
                        errorBoxClass: 'col-sm-offset-4 col-sm-8',
                        errorMessageClass: 'help-block',
                        successWrapperClass: 'has-success',
                        errorWrapperClass: 'has-error',
                        fieldWrapper: '.form-group'
                    }
        });
        webshims.polyfill('forms');

        $(document).ready(function() {
            $('#newPassword').on('valuevalidation', function(e, extra) {
                var val = $.trim(extra.value);
                if (val.length < 6) {
                    return 'Please enter at least 6 characters.';
                }
            });
        });
    </script>
</head>
<body>
<div class="clearfix"></div>
<div class="container" ng-app="bendyApp" ng-controller="BendySettingsCtrl">
    <div ng-show="message" class="message">{{message}}</div>
    <div ng-show="errors" class="errors">
        <ul>
            <li ng-repeat="error in errors">{{error.message}}</li>
        </ul>
    </div>
    <form name="settingsForm" id="settingsForm" class="form-horizontal ws-validate" role="form" ng-submit="update(settingsCommand)">
        <div ng-controller="BendyDirtyFormCtrl">
            <div class="form-group">
                <label for="loginEmail" class="col-sm-4 control-label">Email Address (for sign in)</label>
                <div class="col-sm-8">
                    <input type="email" class="form-control"
                           id="loginEmail" name="loginEmail" ng-model="settingsCommand.loginEmail" bendy-dirty
                           size="42" required placeholder="john@example.com"/>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-4">
                    <a href="#" class="expander btn btn-default" role="button"
                       ng-hide="settingsCommand.changePassword" ng-click="changePassword()">&gt; Change Password</a>
                    <a href="#" class="expander btn btn-default" role="button"
                       ng-show="settingsCommand.changePassword" ng-click="cancelPasswordChange()">V Cancel password change</a>
                </div>
            </div>
            <div class="row">
                <div collapse="!settingsCommand.changePassword">
                    <fieldset id="changePasswordSection" class="well well-large">
                        <div class="form-group">
                            <label for="oldPassword" class="col-sm-4 control-label">Current Password</label>
                            <div class="col-sm-8">
                                <input type="text" id="oldPassword" name="oldPassword"
                                       ng-model="settingsCommand.oldPassword" bendy-dirty autocomplete="off" ng-required="settingsCommand.changePassword"
                                       class="form-control"
                                       data-ajaxvalidate="validateCurrentPassword"
                                       data-errormessage-ajaxvalidate="Current password is different.  Please try again."/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="newPassword" class="col-sm-4 control-label">New Password</label>
                            <div class="col-sm-8">
                                <input type="text" id="newPassword" name="newPassword"
                                       ng-model="settingsCommand.newPassword" bendy-dirty autocomplete="off" ng-required="settingsCommand.changePassword"
                                       class="form-control" maxlength="40"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="newPasswordConfirm" class="col-sm-4 control-label">Confirm New Password</label>
                            <div class="col-sm-8">
                                <input type="text" id="newPasswordConfirm" name="newPasswordConfirm"
                                       ng-model="settingsCommand.newPasswordConfirm" bendy-dirty autocomplete="off" ng-required="settingsCommand.changePassword"
                                       class="form-control"
                                       data-dependent-validation="newPassword" data-errormessage-dependent="This does not match your New Password."/>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>

            <div class="form-group">
                <label for="timeZone" class="col-sm-4 control-label">Time Zone</label>
                <div class="col-sm-8">
                    <select id="timeZone" name="timeZone"
                            ng-model="settingsCommand.timeZone" bendy-dirty ng-options="o.key as o.value for o in timeZoneOptions"
                            required class="form-control"></select>
                </div>
            </div>

            <div class="form-group">
                <label for="dateFormat" class="col-sm-4 control-label">Date Format</label>
                <div class="col-sm-8">
                    <select id="dateFormat" name="dateFormat"
                            ng-model="settingsCommand.dateFormat" bendy-dirty ng-options="o.key as o.value for o in dateFormatOptions"
                            required class="form-control"></select>
                </div>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-4 col-sm-8">
                <button type="submit" class="save btn btn-default"
                        ng-disabled="!settingsForm.$dirty"
                        ng-class="{changed: settingsForm.$dirty}">
                    Save
                </button>
            </div>
        </div>
    </form>
</div>
</body>
</html>
