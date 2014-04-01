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
</head>
<body>
<div class="body" ng-app="bendyApp" ng-controller="BendySettingsCtrl">
    <div ng-show="message" class="message">{{message}}</div>
    <div ng-show="errors" class="errors">
        <ul>
            <li ng-repeat="error in errors">{{error.message}}</li>
        </ul>
    </div>
    <form name="settingsForm">
        <div class="dialog" ng-controller="BendyDirtyFormCtrl">
            <table>
                <tbody>

                <tr class="prop">
                    <td class="required name">
                        <label for="loginEmail">Email Address (for log in)</label>
                    </td>
                    <td class="required value">
                        <input type="email" id="loginEmail" name="loginEmail" ng-model="settingsCommand.loginEmail" bendy-dirty size="42" required/> <wcy:required/>
                    </td>
                </tr>

                <tr>
                    <td colspan="2">
                        <a href="#" class="expander" ng-hide="settingsCommand.changePassword" ng-click="changePassword()">&gt; Change Password</a>
                        <a href="#" class="expander" ng-show="settingsCommand.changePassword" ng-click="cancelPasswordChange()">V Cancel password change</a>
                        <div collapse="!settingsCommand.changePassword">
                            <div class="dialog well well-large">
                                <table>
                                    <tbody>

                                    <tr class="prop">
                                        <td class="required name">
                                            <label for="oldPassword">Current Password</label>
                                        </td>
                                        <td class="required value">
                                            <input type="text" id="oldPassword" name="oldPassword"
                                                   ng-model="settingsCommand.oldPassword" bendy-dirty autocomplete="off" ng-required="settingsCommand.changePassword"/> <wcy:required/>
                                        </td>
                                    </tr>

                                    <tr class="prop">
                                        <td class="required name">
                                            <label for="newPassword">New Password</label>
                                        </td>
                                        <td class="required value">
                                            <input type="text" id="newPassword" name="newPassword"
                                                   ng-model="settingsCommand.newPassword" bendy-dirty autocomplete="off" ng-required="settingsCommand.changePassword"/> <wcy:required/>
                                        </td>
                                    </tr>

                                    <tr class="prop">
                                        <td class="required name">
                                            <label for="newPasswordConfirm">Confirm New Password</label>
                                        </td>
                                        <td class="required value">
                                            <input type="text" id="newPasswordConfirm" name="newPasswordConfirm"
                                                   ng-model="settingsCommand.newPasswordConfirm" bendy-dirty autocomplete="off" ng-required="settingsCommand.changePassword"/> <wcy:required/>
                                        </td>
                                    </tr>

                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="required name">
                        <label for="timeZone">Time Zone</label>
                    </td>
                    <td class="required value">
                        <select id="timeZone" name="timeZone"
                                ng-model="settingsCommand.timeZone" bendy-dirty ng-options="o.key as o.value for o in timeZoneOptions" required></select> <!-- redundant wcy:required/-->
                    </td>
                </tr>

                <tr class="prop">
                    <td class="required name">
                        <label for="dateFormat">Date Format</label>
                    </td>
                    <td class="required value">
                        <select id="dateFormat" name="dateFormat"
                                ng-model="settingsCommand.dateFormat" bendy-dirty ng-options="o.key as o.value for o in dateFormatOptions" required></select> <!-- redundant wcy:required/-->
                    </td>
                </tr>

                <tr>
                    <td colspan="2">
                        <wcy:requiredLegend/>
                    </td>
                </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button">
                <button class="save"
                        ng-click="update(settingsCommand)"
                        ng-disabled="settingsForm.$invalid || !settingsForm.$dirty"
                        ng-class="{changed: settingsForm.$dirty}">
                    Save
                </button>
            </span>
        </div>
    </form>
</div>
</body>
</html>
