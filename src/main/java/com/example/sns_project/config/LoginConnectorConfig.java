package com.example.sns_project.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginConnectorConfig {

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createLoginConnector());
        return tomcat;
    }

    private Connector createLoginConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(8081);

        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        protocol.setMaxThreads(4);
        protocol.setMinSpareThreads(2);
        protocol.setAcceptCount(200);

        return connector;
    }
}