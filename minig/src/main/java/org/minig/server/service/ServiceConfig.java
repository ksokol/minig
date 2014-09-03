package org.minig.server.service;

import org.minig.server.service.submission.JavaMailSenderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Kamill Sokol
 */
@Configuration
@ComponentScan(basePackages = "org.minig.server.service")
@Profile({ "prod" })
public class ServiceConfig {

    @Bean
    public JavaMailSenderFactory javaMailSenderFactory() {
        return new JavaMailSenderFactory();
    }

}
