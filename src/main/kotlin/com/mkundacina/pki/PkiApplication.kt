package com.mkundacina.pki

import org.apache.catalina.Context
import org.apache.catalina.connector.Connector
import org.apache.tomcat.util.descriptor.web.SecurityCollection
import org.apache.tomcat.util.descriptor.web.SecurityConstraint
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.apache.logging.log4j.kotlin.logger

object logger { fun get() = org.apache.logging.log4j.kotlin.logger("MyLogger")}

@SpringBootApplication
class PkiApplication
fun main(args: Array<String>) {
    runApplication<PkiApplication>(*args)
    logger.get().info("----------------------------------------")
    logger.get().info("AppStarted")
}

@Bean
fun servletContainer(): ServletWebServerFactory? {
    // Enable SSL Trafic
    val tomcat: TomcatServletWebServerFactory = object : TomcatServletWebServerFactory() {
        override fun postProcessContext(context: Context) {
            val securityConstraint = SecurityConstraint()
            securityConstraint.userConstraint = "CONFIDENTIAL"
            val collection = SecurityCollection()
            collection.addPattern("/*")
            securityConstraint.addCollection(collection)
            context.addConstraint(securityConstraint)
        }
    }

    // Add HTTP to HTTPS redirect
    tomcat.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector())
    return tomcat
}

/*
    We need to redirect from HTTP to HTTPS. Without SSL, this application used
    port 8080. With SSL it will use port 8089. So, any request for 8080 needs to be
    redirected to HTTPS on 8089.
     */
private fun httpToHttpsRedirectConnector(): Connector? {
    val connector = Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL)
    connector.scheme = "http"
    connector.port = 8080
    connector.secure = false
    connector.redirectPort = 8089
    return connector
}
