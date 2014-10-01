%{--
  - Copyright (c) 2010 J. David Beutel <software@getsu.com>
  -
  - Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
  --}%
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="unauth"/>
    <title>Bendy | Login</title>
</head>
<body>
<auth:ifLoggedIn>
    You are currently signed in as: <auth:user/>
    <g:if test="${flash.authenticationFailure}">
        <div class="errors alert alert-danger center-block">
            <ul><li>Sorry but your sign out failed - reason: <g:message code="authentication.failure.${flash.authenticationFailure.result}"/></li></ul>
        </div>
    </g:if>
    <auth:form authAction="logout" success="[controller:'auth', action:'login']" error="[controller:'auth', action:'login']">
        <g:actionSubmit value="Sign out" class="btn btn-lg btn-primary btn-block"/>
    </auth:form>
</auth:ifLoggedIn>
%{--
<auth:ifUnconfirmed>
You've registered but we're still waiting to confirm your account. <g:link action="reconfirm">Click here to send a new confirmation request</g:link> if you missed it the first time.
</auth:ifUnconfirmed>
--}%
<auth:ifNotLoggedIn>
    <g:if test="${flash.authenticationFailure || flash.loginFormErrors && hasErrors(bean: flash.loginFormErrors, 'true')}">
        %{-- get all the errors together into the same list for display --}%
        <g:if test="${flash.authenticationFailure}">
            %{ flash.loginFormErrors = flash.loginFormErrors ?: flash.loginForm.errors;
               flash.loginFormErrors.reject(
                "authentication.failure.${flash.authenticationFailure.result}",
                "Sign in failed: " + message(code:"authentication.failure.${flash.authenticationFailure.result}")
            ) }%
        </g:if>
        <div class="errors alert alert-danger col-sm-offset-2 col-sm-8">
            <g:renderErrors bean="${flash.loginFormErrors}" as="list"/>
        </div>
    </g:if>

    <div class="col-sm-12">
        <auth:form authAction="login" success="[controller:'settings', action:'appIndex']"
                   error="[controller:'auth', action:'login']" role="form" class="login">
            <div class="well well-lg">
                <p class="h3">Please sign in</p>
                <input type="email" id="login" name="login" size="42" value="${flash.loginForm?.login?.encodeAsHTML()}"
                            placeholder="Email address" required="" autofocus="" class="form-control"/>
                <input id="password" name="password" value="" type="password"
                            placeholder="Password" required="" class="form-control"/>
                <button type="submit" class="btn btn-lg btn-primary btn-block">Sign in</button>
            </div>

            <div class="text-right">
                <g:link action="forgot">Forgot password</g:link>
            </div>
            %{--<div>--}%
                %{--<g:link action="signup">Sign up for new account</g:link>--}%
            %{--</div>--}%
        </auth:form>
    </div>
</auth:ifNotLoggedIn>
</body>
</html>