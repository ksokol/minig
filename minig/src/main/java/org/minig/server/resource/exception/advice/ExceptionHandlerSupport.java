package org.minig.server.resource.exception.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kamill Sokol
 */
class ExceptionHandlerSupport {

    protected ResponseEntity<Map<String, Object>> transform(String statusMessage, HttpStatus httpStatus) {

        Map<String, Object> data = new HashMap<>();
        data.put("status", httpStatus.value());
        data.put("message", statusMessage);
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(data, httpStatus);
        return response;
    }
}
