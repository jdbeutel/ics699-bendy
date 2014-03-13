%{--
- Copyright (c) 2010 J. David Beutel <software@getsu.com>
-
- Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
--}%
<%@ page import="com.getsu.wcy.Person" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>My Profile</title>
    <r:require module="angular"/>
</head>
<body>
<div class="body">
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${personInstance}">
        <div class="errors">
            <g:renderErrors bean="${personInstance}" as="list"/>
        </div>
    </g:hasErrors>
    %{--<g:uploadForm name="htmlForm" method="post" ng-app="bendyApp" bendy-change-manager="">--}%
    <g:uploadForm name="htmlForm" method="post" ng-app="bendyApp" ng-controller="BendyChangeCtrl">
        <div class="dialog">
            <table>
                <tbody>
                <g:render template="/person/editCore"/>
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
                <g:actionSubmit class="save" action="updateMyProfile" ng-disabled="!dirty" ng-class="{changed: dirty}"
                                value="${message(code: 'default.button.update.label', default: 'Save')}"/>
                dirty: {{dirty}}, changed: {{changed}}
            </span>
        </div>
    </g:uploadForm>
</div>
</body>
</html>
