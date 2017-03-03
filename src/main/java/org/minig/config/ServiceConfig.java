package org.minig.config;

import org.minig.server.converter.MessageToCompositeAttachmentIdConverter;
import org.minig.server.converter.Mime4jAttachmentToMailAttachmentConverter;
import org.minig.server.service.submission.JavaMailSenderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;

import java.util.HashSet;
import java.util.Set;

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

    @Bean
    public ConversionServiceFactoryBean conversionService() {
        ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
        Set<Converter> converters = new HashSet<>();
        converters.add(new MessageToCompositeAttachmentIdConverter());
        converters.add(new Mime4jAttachmentToMailAttachmentConverter());
        conversionServiceFactoryBean.setConverters(converters);
        return conversionServiceFactoryBean;
    }
}
