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
    <title>Signed out</title>
</head>
<body>
<div class="body">
    <auth:ifLoggedIn>
        <div class="errors alert alert-danger">
            <ul><li>Sorry but your sign out failed - reason: <g:message code="authentication.failure.${flash.authenticationFailure.result}"/></li></ul>
        </div>
    </auth:ifLoggedIn>
    <auth:ifNotLoggedIn>
        <div class="alert alert-success col-sm-offset-2 col-sm-8">
            <span class="glyphicon glyphicon-ok"></span> Sign out succeeded
        </div>

        <div class="col-sm-offset-8 col-sm-4">
            <g:link action="login">Sign back in again</g:link><br/>
        </div>
    </auth:ifNotLoggedIn>
</div>
</body>
</html>
