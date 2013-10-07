package org.minig.server.resource;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Component("handlerExceptionResolver")
public class CustomExceptionResolver implements HandlerExceptionResolver {

    private static final Logger logger = Logger.getLogger(CustomExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object object, Exception exception) {
        int status = 500;

        Map<String, Object> data = new HashMap<String, Object>();
        Object msg = null;

        if (exception != null) {
            msg = exception.getMessage();
            logger.error(exception.getMessage(), exception);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            data.put("status", status);
            if (msg != null) data.put("message", msg);

            response.setStatus(status);
            response.setContentType("application/json");

            objectMapper.writeValue(response.getOutputStream(), data);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return new ModelAndView();
    }
}