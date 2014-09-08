package org.minig.server.resource.exception.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * @author Kamill Sokol
 */
@ControllerAdvice
public class MissingServletRequestParameterExceptionAdvice extends ExceptionHandlerSupport {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> exception(MissingServletRequestParameterException e) {
        HttpStatus statusCode = HttpStatus.BAD_REQUEST;
        String statusMessage = e.getMessage();
        return transform(statusMessage, statusCode);
    }

}
