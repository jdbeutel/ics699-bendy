modules = {
    application {
//        resource url: [plugin: "prototype", dir:"js/prototype", file: "prototype.js"], disposition: "head"
//        resource url: [plugin: "prototype", dir:"js/prototype", file: "scriptaculous.js"], disposition: "head"
//        resource url: [plugin: "prototype", dir:"js/prototype", file: "effects.js"], disposition: "head"
        resource url:'js/application.js'
        resource url:'css/navigation.css', disposition: "head"
    }
    angular {
        dependsOn 'jquery'
        resource url:'/js/lib/angular-1.2.14/angular.js'
        resource url:'/js/angular/app.js'
        resource url:'/js/angular/controllers.js'
        resource url:'/js/angular/directives.js'
    }
}