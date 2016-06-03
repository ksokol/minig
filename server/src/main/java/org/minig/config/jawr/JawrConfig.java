package org.minig.config.jawr;

import freemarker.cache.ClassTemplateLoader;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.template.TemplateException;
import net.jawr.web.servlet.JawrSpringController;
import org.minig.config.jawr.config.CssConfigPropertiesSource;
import org.minig.config.jawr.config.JavascriptConfigPropertiesSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

/**
 * @author Stephane Nicoll
 * @author Kamill Sokol
 */
public class JawrConfig {

    @Bean
    public SimpleUrlHandlerMapping urlMapping() {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setOrder(Integer.MAX_VALUE - 2);
        Properties p = new Properties();

        p.setProperty("/css/**", "jawrCssController");
        p.setProperty("/js/**", "jawrJavascriptController");

        simpleUrlHandlerMapping.setMappings(p);

        return simpleUrlHandlerMapping;
    }

    @Bean
    public JawrSpringController jawrCssController() {
        JawrSpringController jawrSpringController = new JawrSpringController();
        jawrSpringController.setControllerMapping("/css");
        jawrSpringController.setConfigPropertiesSourceClass(CssConfigPropertiesSource.class.getCanonicalName());
        jawrSpringController.setType("css");
        return jawrSpringController;
    }

    @Bean
    public JawrSpringController jawrJavascriptController() {
        JawrSpringController jawrSpringController = new JawrSpringController();
        jawrSpringController.setControllerMapping("/js");
        jawrSpringController.setConfigPropertiesSourceClass(JavascriptConfigPropertiesSource.class.getCanonicalName());
        jawrSpringController.setType("js");
        return jawrSpringController;
    }

    // https://github.com/spring-projects/spring-boot/issues/907
    @Bean
    @Autowired
    public freemarker.template.Configuration freeMarkerConfig(ServletContext servletContext) throws IOException, TemplateException {
        FreeMarkerConfigurer freemarkerConfig = configFreeMarkerConfigurer(servletContext);
        return freemarkerConfig.getConfiguration();
    }

    @Bean
    @Autowired
    public TaglibFactory taglibFactory(ServletContext servletContext) throws IOException, TemplateException {
        FreeMarkerConfigurer freemarkerConfig = configFreeMarkerConfigurer(servletContext);
        return freemarkerConfig.getTaglibFactory();
    }

    @Autowired
    @Bean
    public FreeMarkerConfig springFreeMarkerConfig(ServletContext servletContext) throws IOException, TemplateException {
        return new MyFreeMarkerConfig(freeMarkerConfig(servletContext), taglibFactory(servletContext));
    }

    private static FreeMarkerConfigurer configFreeMarkerConfigurer(ServletContext servletContext) throws IOException, TemplateException {
        FreeMarkerConfigurer freemarkerConfig = new FreeMarkerConfigurer();
        freemarkerConfig.setPreTemplateLoaders(new ClassTemplateLoader(JawrConfig.class, "/templates/"));
        ServletContext servletContextProxy = (ServletContext) Proxy.newProxyInstance(ServletContextResourceHandler.class.getClassLoader(), new Class<?>[]{ServletContext.class},
                new ServletContextResourceHandler(servletContext));
        freemarkerConfig.setServletContext(servletContextProxy);
        Properties settings = new Properties();
        settings.put("default_encoding", "UTF-8");
        freemarkerConfig.setFreemarkerSettings(settings);
        freemarkerConfig.afterPropertiesSet();
        return freemarkerConfig;
    }

    @Bean
    public FreeMarkerViewResolver viewResolver() {
        FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
        viewResolver.setCache(false);
        viewResolver.setSuffix(".ftl");
        viewResolver.setContentType("text/html;charset=UTF-8");
        return viewResolver;
    }

    private static class ServletContextResourceHandler implements InvocationHandler {
        private final ServletContext target;

        private ServletContextResourceHandler(ServletContext target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ( "getResourceAsStream".equals(method.getName()) ) {
                Object result = method.invoke(target, args);
                if ( result == null ) {
                    result = JawrConfig.class.getResourceAsStream((String) args[0]);
                }
                return result;
            } else if ( "getResource".equals(method.getName()) ) {
                Object result = method.invoke(target, args);
                if ( result == null ) {
                    result = JawrConfig.class.getResource((String) args[0]);
                }
                return result;
            }
            return method.invoke(target, args);
        }
    }

    private static class MyFreeMarkerConfig implements FreeMarkerConfig {
        private final freemarker.template.Configuration configuration;
        private final TaglibFactory                     taglibFactory;

        private MyFreeMarkerConfig(freemarker.template.Configuration configuration, TaglibFactory taglibFactory) {
            this.configuration = configuration;
            this.taglibFactory = taglibFactory;
        }

        @Override
        public freemarker.template.Configuration getConfiguration() {
            return configuration;
        }

        @Override
        public TaglibFactory getTaglibFactory() {
            return taglibFactory;
        }
    }
}
