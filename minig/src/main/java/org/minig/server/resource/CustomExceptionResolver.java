package org.minig.server.resource;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CustomExceptionResolver implements HandlerExceptionResolver {

    private static final Logger logger = Logger.getLogger(CustomExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object object, Exception exception) {
        if(exception.getClass().getAnnotation(ResponseStatus.class) != null) {
            return null;
        }

        //TODO naive approach. what about other exceptions?
        if(exception.getClass().isAssignableFrom(MissingServletRequestParameterException.class)) {
            return null;
        }

        int status = 500;

        Map<String, Object> data = new HashMap<>();
        Object msg = null;

        if (exception != null) {
            msg = exception.getMessage();
            logger.error(exception.getMessage(), exception);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            data.put("status", status);
            if (msg != null) {
                data.put("message", msg);
            }

            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            objectMapper.writeValue(response.getOutputStream(), data);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return new ModelAndView();
    }

}