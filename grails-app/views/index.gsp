<html>
<head>
    <meta name="layout" content="angular-main"/>
</head>

<body>
<nav class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle"
                    ng-init="navbarCollapse=true" ng-click="navbarCollapse = !navbarCollapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <p class="navbar-brand">Bendy</p>
            <p class="navbar-text visible-xs" ng-show="navbarCollapse">{{pageTitle}}</p>
        </div>

        <div class="navbar-collapse" collapse="navbarCollapse">
            <ul class="nav navbar-nav">
                <li ng-class="{active: pageTitle == 'Contacts'}">
                    <a href="#/contacts">Contacts</a>
                </li>
                <li ng-class="{active: pageTitle == 'Notifications'}">
                    <a href="#/notifications">Notifications</a>
                </li>
                <li ng-class="{active: pageTitle == 'Groups'}">
                    <a href="#/groups">Groups</a>
                </li>
                <li ng-class="{active: pageTitle == 'My Profile'}">
                    <a href="#/profile">My Profile</a>
                </li>
                <li ng-class="{active: pageTitle == 'Settings'}">
                    <a href="#/settings">Settings</a>
                </li>
            </ul>
            <ul  class="nav navbar-nav navbar-right">
                <li>
                    <a href="/auth/logout">Sign Out</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div ng-view></div>
</body>
</html>