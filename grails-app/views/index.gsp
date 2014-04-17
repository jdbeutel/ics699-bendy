<html>
<head>
    <meta name="layout" content="angular-main"/>
</head>

<body>
<div id="auth">
    <nav:menu scope="authOptions" class="navigation"/>
</div>

<div class="logo"><h1>Bendy</h1></div>
<ul class="nav nav-tabs">
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

<div ng-view></div>
</body>
</html>