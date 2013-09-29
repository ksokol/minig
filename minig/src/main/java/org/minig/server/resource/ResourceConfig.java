package org.minig.server.resource;

import java.util.List;

import org.minig.server.resource.argumentresolver.CompositeIdHandlerMethodArgumentResolver;
import org.minig.server.resource.argumentresolver.StringIdHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan(basePackages = "org.minig.server.resource")
@EnableWebMvc
@Profile({ "test", "prod" })
public class ResourceConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON).ignoreAcceptHeader(true).useJaf(false).favorPathExtension(false);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new StringIdHandlerMethodArgumentResolver());
        argumentResolvers.add(new CompositeIdHandlerMethodArgumentResolver());
    }

    @Bean(name = "multipartResolver")
    public MultipartResolver getMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        // multipartResolver.setMaxUploadSize(100000);
        return multipartResolver;
    }

}
