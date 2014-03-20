modules = {
    application {
        dependsOn 'bootstrap-css'
        resource url:'js/application.js'
        resource url:'css/navigation.css', disposition: "head"
    }
    angular {
        dependsOn 'jquery'
        resource url:'/js/lib/angular-1.2.14/angular.js'
        resource url:'/js/lib/angular-1.2.14/angular-resource.js'
        resource url:'/js/lib/angular-ui/bootstrap/ui-bootstrap-tpls-0.10.0.js'
        resource url:'/js/angular/app.js'
        resource url:'/js/angular/controllers.js'
        resource url:'/js/angular/services.js'
        resource url:'/js/angular/directives.js'
    }
}