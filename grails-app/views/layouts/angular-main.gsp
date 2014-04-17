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
        </script>
		<g:layoutHead/>
	</head>
	<body>
        <div class="container">
            <g:layoutBody/>
            <div class="footer" role="contentinfo"></div>
            <div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
        </div>
		<r:layoutResources />
	</body>
</html>
