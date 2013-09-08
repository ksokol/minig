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

    @Bean(name = "javaMailProperties")
    public Properties javaMailProperties() {
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.store.protocol", "imap");
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.imap.port", "3143");
        javaMailProperties.put("mail.transport.protocol", "smtp");
        javaMailProperties.put("mail.smtp.port", "3125");
        // javaMailProperties.put("mail.debug", "true");

        return javaMailProperties;
    }

    // @Bean(name = "javaMail")
    // public JavaMailSender javaMail() {
    // JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
    // javaMailSenderImpl.setJavaMailProperties(javaMailProperties());
    //
    // return javaMailSenderImpl;
    // }

    @Bean(name = "mailContext")
    public MailContext mailContext() {
        return new SimpleMailContextImpl();
    }
}
