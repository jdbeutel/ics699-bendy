import org.apache.catalina.valves.AccessLogValve

// Configure access log for manually testing run-app in development,
// similar to how tomcat/conf/server.xml configures it on its98 & its20;
// see http://stackoverflow.com/questions/2122070/configuring-grails-access-logging-from-run-app
eventConfigureTomcat = { tomcat ->
    tomcat?.host?.addValve new AccessLogValve(
            directory: "$basedir/target/logs",
            prefix: 'localhost_access_log.',
            suffix: '.txt',
            pattern: '%{X-Forwarded-For}i [%{username}s %I] %t "%r" %s %b %Dms'
    )
}
