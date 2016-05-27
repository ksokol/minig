package org.minig.server.service;

import org.minig.config.ServiceConfig;
import org.minig.server.service.impl.MailContext;
import org.minig.server.service.impl.SimpleMailContextImpl;
import org.minig.server.service.submission.TestJavaMailSenderFactory;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ConversionServiceFactoryBean;

@Configuration
@ComponentScan(basePackages = "org.minig.server.service")
@Profile({ "test" })
public class ServiceTestConfig {

    @Bean(name = "mailContext")
    public MailContext mailContext() {
        return new SimpleMailContextImpl();
    }

    @Bean(name = "javaMailSenderFactory")
    public TestJavaMailSenderFactory javaMailSenderFactory() {
        return new TestJavaMailSenderFactory();
    }

    @Bean
    public ConversionServiceFactoryBean conversionService() {
        return new ServiceConfig().conversionService();
    }
}
