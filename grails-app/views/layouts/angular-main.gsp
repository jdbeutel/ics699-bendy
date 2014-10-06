<!DOCTYPE html>
<html lang="en" ng-app="bendyApp" ng-controller="BendyAppCtrl" class="no-js">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title ng-bind="'Bendy | ' + pageTitle"></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">
        <r:require module="application"/>
        <r:require module="angular"/>
        <r:layoutResources />
        %{-- js-webshim loads more files dynamically, so needs non-static path, cannot go in ApplicationResources. --}%
        <script src="${resource(dir:'js/lib/js-webshim-1.12.5/dev/extras', file:'modernizr-custom.js')}" ></script>
        <script src="${resource(dir:'js/lib/js-webshim-1.12.5/dev', file:'polyfiller.js')}" ></script>
        <link rel="stylesheet" href="${resource(dir:'css', file:'bendy.css')}" type="text/css" media="screen, projection">
        <g:render template="/layouts/webshimValidate" model="[indent: true]"/>
		<g:layoutHead/>
	</head>
	<body>
        <div class="container">
            <g:layoutBody/>
            <div class="footer" role="contentinfo"></div>
            <div id="spinner" class="spinner" style="display:none;">Loading&hellip;</div>
        </div>
		<r:layoutResources />
	</body>
</html>
