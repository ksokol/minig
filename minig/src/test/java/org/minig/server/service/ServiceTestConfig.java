package org.minig.server.service;

import java.util.Properties;

import org.minig.server.service.impl.MailContext;
import org.minig.server.service.impl.SimpleMailContextImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan(basePackages = "org.minig.server.service")
@Profile({ "test" })
public class ServiceTestConfig {

    @Bean(name = "mailContext")
    public MailContext mailContext() {
        return new SimpleMailContextImpl();
    }
}
