%{--
  - Copyright (c) 2010 J. David Beutel <software@getsu.com>
  -
  - Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
  --}%
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="unauth"/>
    <title>Sign up</title>
</head>
<body>
<div class="body">
    <auth:ifLoggedIn>
        You are currently logged in as: <auth:user/>
        <g:if test="${flash.authenticationFailure}">
            <div class="errors">
                <ul><li>Sorry but your logout failed - reason: <g:message code="authentication.failure.${flash.authenticationFailure.result}"/></li></ul>
            </div>
        </g:if>
        <h2>Log out</h2>
        <auth:form authAction="logout" success="[controller:'login', action:'signup']" error="[controller:'login', action:'signup']">
            <div class="buttons">
                <g:actionSubmit value="Log out"/>
            </div>
        </auth:form>
    </auth:ifLoggedIn>

    <auth:ifNotLoggedIn>
        <div class="body">
            <h2>Please sign up</h2>
            <g:if test="${flash.message}">
                <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${personInstance}">
                <div class="errors">
                    <g:renderErrors bean="${personInstance}" as="list" />
                </div>
            </g:hasErrors>
            <g:hasErrors bean="${signupForm}">
                <div class="errors">
                    <g:renderErrors bean="${signupForm}" as="list" />
                </div>
            </g:hasErrors>
            <g:form action="doSignup" method="post" >
                <div class="dialog">

                    <table>
                        <tbody>
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="login">Email</label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: signupForm, field: 'login', 'errors')}">
                                <g:textField name="login" size="42" value="${signupForm?.login}" />
                            </td>
                        </tr>

                        <g:hiddenField name="email" value="ignored@example.com"/>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="password">Password</label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: signupForm, field: 'password', 'errors')}">
                                <g:passwordField name="password" value="" />
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="passwordConfirm">Confirm password</label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: signupForm, field: 'passwordConfirm', 'errors')}">
                                <g:passwordField name="passwordConfirm" value="" />
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="preferredName"><g:message code="person.preferredName.label" default="Preferred Name" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: personInstance, field: 'preferredName', 'errors')}">
                                <g:textField name="preferredName" value="${personInstance?.preferredName}" />
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="honorific"><g:message code="person.honorific.label" default="Honorific" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: personInstance, field: 'honorific', 'errors')}">
                                <g:textField name="honorific" value="${personInstance?.honorific}" />
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="givenNames"><g:message code="person.givenNames.label" default="Given Names" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: personInstance, field: 'givenNames', 'errors')}">
                                <g:textField name="givenNames" value="${personInstance?.givenNames}" />
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="familyName"><g:message code="person.familyName.label" default="Family Name" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: personInstance, field: 'familyName', 'errors')}">
                                <g:textField name="familyName" value="${personInstance?.familyName}" />
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="suffix"><g:message code="person.suffix.label" default="Suffix" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: personInstance, field: 'suffix', 'errors')}">
                                <g:textField name="suffix" value="${personInstance?.suffix}" />
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="photo"><g:message code="person.photo.label" default="Photo" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: personInstance, field: 'photo', 'errors')}">

                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="birthDate"><g:message code="person.birthDate.label" default="Birth Date" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: personInstance, field: 'birthDate', 'errors')}">
                                <g:datePicker name="birthDate" precision="day" value="${personInstance?.birthDate}" noSelection="['': '']" />
                            </td>
                        </tr>

                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="doSignup" class="save" value="Sign up" /></span>
                </div>
            </g:form>
        </div>
    </auth:ifNotLoggedIn>
</div>
</body>
</html>