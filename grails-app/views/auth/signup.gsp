%{--
  - Copyright (c) 2010 J. David Beutel <software@getsu.com>
  -
  - Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
  --}%
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="signup"/>
    <title>Sign up</title>
    <r:require module="unauth"/>
</head>
<body>
<div class="body">
    <auth:ifLoggedIn>
        You are currently signed in as: <auth:user/>
        <g:if test="${flash.authenticationFailure}">
            <div class="errors alert alert-danger">
                <ul><li>Sorry but your sign out failed - reason: <g:message code="authentication.failure.${flash.authenticationFailure.result}"/></li></ul>
            </div>
        </g:if>
        <g:form action="logout" method="post">
            <button type="submit" class="btn btn-lg btn-primary">Sign out</button>
        </g:form>
    </auth:ifLoggedIn>

    <auth:ifNotLoggedIn>
        <g:if test="${alreadySignedUp}">
            <div class="alert alert-warning">
                <span class="glyphicon glyphicon-warning-sign"></span>
                ${alreadySignedUp.login} has already signed up.
            </div>
            <h3>Please <g:link action="login">sign in</g:link> instead.</h3>
        </g:if>
        <g:else>
            <h3>Welcome, ${invitation.person.name}!</h3>
            <h4>Please choose a password.</h4>
            <g:form action="doSignup" method="post" name="signupForm" class="form-horizontal ws-validate" role="form">
                <g:hiddenField name="invitation.id" value="${invitation.id}" />

                <div class="form-group">
                    <label class="col-sm-4 control-label">Email Address (for sign in)</label>
                    <div class="col-sm-8">
                        <p class="form-control-static">${invitation.email}</p>
                    </div>
                </div>

                <div class="form-group">
                    <label for="password" class="col-sm-4 control-label">Password</label>
                    <div class="col-sm-8">
                        <input type="text" id="password" name="password"
                               autocomplete="off" required
                               class="form-control" maxlength="40"/>
                    </div>
                </div>

                <div class="form-group">
                    <label for="passwordConfirm" class="col-sm-4 control-label">Confirm Password</label>
                    <div class="col-sm-8">
                        <input type="text" id="passwordConfirm" name="passwordConfirm"
                               autocomplete="off" required
                               class="form-control"
                               data-dependent-validation="password"
                               data-errormessage-dependent="This does not match the above password."/>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-sm-offset-4 col-sm-2">
                        <button type="submit" class="save btn btn-primary">
                            Sign up
                        </button>
                    </div>
                </div>
            </g:form>
        </g:else>
    </auth:ifNotLoggedIn>
</div>

<script>
    $(document).ready(function() {
        $('#password').on('valuevalidation', function(e, extra) {
            var val = $.trim(extra.value);
            if (val.length < 6) {
                return 'Please enter at least 6 characters.';
            }
        });
    });
</script>
</body>
</html>