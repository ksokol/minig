package org.minig.config.jawr;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jawr.web.JawrConstant;
import net.jawr.web.context.ThreadLocalJawrContext;
import net.jawr.web.resource.bundle.factory.util.PathNormalizer;
import net.jawr.web.servlet.JawrBinaryResourceRequestHandler;
import net.jawr.web.servlet.JawrRequestHandler;
import net.jawr.web.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.UrlPathHelper;

/**
 * A Spring Controller implementation which uses a JawrRequestHandler instance
 * to provide with Jawr functionality within a Spring DispatcherServlet
 * instance.
 *
 * @author Jordi Hernández Sellés
 * @author Ibrahim Chaehoi
 * @author Kamill Sokol
 */
class CustomJawrSpringController implements Controller, ServletContextAware, InitializingBean, ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomJawrSpringController.class);
    private JawrRequestHandler requestHandler;
    private Map<String, Object> initParams;
    private String type;
    private String configPropertiesSourceClass;
    private String mapping;
    private String controllerMapping;
    private final UrlPathHelper helper = new UrlPathHelper();
    private Properties configuration;
    private String configLocation;
    private ServletContext context;

    public void setServletContext(ServletContext context) {
        this.context = context;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getInitParams() {
        return initParams;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setConfigPropertiesSourceClass(String configPropertiesSourceClass) {
        this.configPropertiesSourceClass = configPropertiesSourceClass;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public void setConfiguration(Properties configuration) {
        this.configuration = configuration;
    }

    public void setControllerMapping(String controllerMapping) {
        if ( controllerMapping.endsWith("/") ) {
            this.controllerMapping = controllerMapping.substring(0, controllerMapping.length() - 1);
        } else {
            this.controllerMapping = controllerMapping;
        }
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String requestedPath = (StringUtils.isEmpty(mapping)) ? helper.getPathWithinApplication(request) : helper.getPathWithinServletMapping(request);

        if ( StringUtils.isNotEmpty(controllerMapping) ) {
            requestedPath = requestedPath.substring(controllerMapping.length() + 1);
        }

        requestHandler.processRequest(requestedPath, request, response);
        return null;
    }

    public void afterPropertiesSet() throws Exception {
        initParams = new HashMap<>(3);
        initParams.put("type", type);
        initParams.put("configPropertiesSourceClass", configPropertiesSourceClass);
        initParams.put("configLocation", configLocation);

        if ( null == configuration && null == configLocation && null == configPropertiesSourceClass )
            throw new ServletException("Neither configuration nor configLocation nor configPropertiesSourceClass init params were set."
                    + " You must set at least the configuration or the configLocation param. Please check your web.xml file");

        String fullMapping = "";
        if ( StringUtils.isNotEmpty(mapping) )
            fullMapping = mapping;

        if ( StringUtils.isNotEmpty(controllerMapping) )
            fullMapping = PathNormalizer.joinPaths(fullMapping, controllerMapping);

        initParams.put(JawrConstant.SERVLET_MAPPING_PROPERTY_NAME, fullMapping);
        if ( mapping != null ) {
            initParams.put(JawrConstant.SPRING_SERVLET_MAPPING_PROPERTY_NAME, PathNormalizer.asDirPath(mapping));
        }

        if ( LOGGER.isDebugEnabled() )
            LOGGER.debug("Initializing Jawr Controller's JawrRequestHandler");

        if ( JawrConstant.BINARY_TYPE.equals(type) ) {
            requestHandler = new JawrBinaryResourceRequestHandler(context, initParams, configuration);
        } else {
            requestHandler = new JawrRequestHandler(context, initParams, configuration);
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ThreadLocalJawrContext.reset();
    }
}
