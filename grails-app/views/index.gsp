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
            <a class="navbar-brand" href="#">Bendy</a>
            <p class="navbar-text active visible-xs" ng-show="navbarCollapse">{{pageTitle}}</p>
        </div>

        <div class="navbar-collapse" collapse="navbarCollapse">
            <ul class="nav navbar-nav">
                <li ng-class="{active: pageTitle == 'Contacts'}">
                    <a href="#/contacts">Contacts</a>
                </li>
                <li ng-class="{active: pageTitle == 'Groups'}">
                    <a href="#/groups">Groups</a>
                </li>
                <li ng-class="{active: pageTitle == 'Profile'}">
                    <a href="#/profile">My Profile</a>
                </li>
                <li ng-class="{active: pageTitle == 'Notifications'}">
                    <a href="#/notifications">Notification</a>
                </li>
                <li ng-class="{active: pageTitle == 'Settings'}">
                    <a href="#/settings">Settings</a>
                </li>
            </ul>
            <nav:menu scope="authOptions" class="nav navbar-nav navbar-right"/>
        </div>
    </div>
</nav>

<div ng-view></div>
</body>
</html>