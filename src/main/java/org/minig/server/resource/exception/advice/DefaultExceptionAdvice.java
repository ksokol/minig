package org.minig.server.resource.exception.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

/**
 * @author Kamill Sokol
 */
@ControllerAdvice
public class DefaultExceptionAdvice extends ExceptionHandlerSupport {

    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> exception(Exception e) {
        log.error(e.getMessage());
        HttpStatus statusCode = getStatusCode(e);
        String statusMessage = getStatusMessage(e);
        return transform(statusMessage, statusCode);
    }

    private HttpStatus getStatusCode(Exception e) {
        ResponseStatus responseStatus = getResponseStatus(e);
        if(responseStatus == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        Object value = AnnotationUtils.getValue(responseStatus, "value");
        return value == null ? HttpStatus.INTERNAL_SERVER_ERROR : (HttpStatus) value;
    }

    private String getStatusMessage(Exception e) {
        ResponseStatus responseStatus = getResponseStatus(e);
        if(responseStatus == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        }
        Object value = AnnotationUtils.getValue(responseStatus, "reason");
        return value == null ? HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(): (String) value;
    }

    private ResponseStatus getResponseStatus(Exception e) {
        if(e == null) {
            return null;
        }
        return e.getClass().getAnnotation(ResponseStatus.class);
    }

}
