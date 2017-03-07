package org.minig.config;

import org.minig.server.service.submission.JavaMailSenderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Kamill Sokol
 */
@Configuration
@Profile({ "dev", "prod" })
public class ServiceConfig {

    @Bean
    public JavaMailSenderFactory javaMailSenderFactory() {
        return new JavaMailSenderFactory();
    }

}
