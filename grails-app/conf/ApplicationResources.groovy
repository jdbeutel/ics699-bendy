modules = {
    application {
        resource url: [plugin: "prototype", dir:"js/prototype", file: "prototype.js"], disposition: "head"
        resource url: [plugin: "prototype", dir:"js/prototype", file: "scriptaculous.js"], disposition: "head"
        resource url: [plugin: "prototype", dir:"js/prototype", file: "effects.js"], disposition: "head"
        resource url:'js/application.js', disposition: "head"    // using a scriptaculous effect
        resource url:'css/navigation.css', disposition: "head"
    }
}