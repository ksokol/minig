package org.minig.server.resource.swagger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.DocumentationTransformer;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.spring.DocumentationReader;
import com.mangofactory.swagger.spring.controller.DocumentationController;
import com.wordnik.swagger.core.Documentation;

//@Controller
@RequestMapping('/' + DocumentationController.CONTROLLER_ENDPOINT)
public class CustomDocumentationController implements ServletContextAware {

    private ServletContext servletContext;

    public static final String CONTROLLER_ENDPOINT = "api-docs";

    @Autowired
    private SwaggerConfiguration swaggerConfiguration;

    @Autowired
    private List<RequestMappingHandlerMapping> handlerMappings;

    private DocumentationReader apiReader;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    Documentation getResourceListing(HttpServletRequest request) throws MalformedURLException {
        Documentation documentation = apiReader.getDocumentation();
        // apiReader2.

        String realPath = test(request);
        documentation.setBasePath(realPath);
        // Documentation documentation = apiReader2.getDocumentation();
        DocumentationTransformer transformer = swaggerConfiguration.getDocumentationTransformer();
        return transformer.applySorting(transformer.applyTransformation(documentation));
    }

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public @ResponseBody
    ControllerDocumentation getApiDocumentation(HttpServletRequest request) throws MalformedURLException {
        String fullUrl = String.valueOf(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));
        int indexOfApiName = fullUrl.indexOf("/", 1) + 1;

        String realPath = realPath = test(request);

        DocumentationTransformer transformer = swaggerConfiguration.getDocumentationTransformer();

        Documentation documentation = apiReader.getDocumentation(fullUrl.substring(indexOfApiName));
        documentation.setBasePath(realPath);

        return (ControllerDocumentation) transformer.applySorting(documentation);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        apiReader = new DocumentationReader(swaggerConfiguration, WebApplicationContextUtils.getWebApplicationContext(servletContext),
                handlerMappings);
        this.servletContext = servletContext;
    }

    private String test(HttpServletRequest request) throws MalformedURLException {

        String file = servletContext.getContextPath();

        URL reconstructedURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), file);

        return reconstructedURL.toString();
    }
}
