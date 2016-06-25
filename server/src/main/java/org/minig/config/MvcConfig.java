package org.minig.config;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Kamill Sokol
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("login").setViewName("login.html");
        registry.addViewController("/").setViewName("index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (environment.acceptsProfiles("dev")) {
            final String userDir = environment.getProperty("user.dir");
            String filePrefix = "file://";

            if (SystemUtils.IS_OS_WINDOWS) {
                filePrefix += "/";
            }

            registry.addResourceHandler("/node_modules/**", "/**")
                    .addResourceLocations(filePrefix + userDir + "/node_modules/")
                    .addResourceLocations(filePrefix + userDir + "/src/main/resources/static/");
        }
    }
}
