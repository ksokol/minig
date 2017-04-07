package org.minig.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author Kamill Sokol
 */
public final class JsonRequestPostProcessors {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonRequestPostProcessors() {
    }

    public static RequestPostProcessor jsonBody(Object object) {
        return request -> {
            request.setContentType(APPLICATION_JSON_UTF8_VALUE);
            try {
                request.setContent(objectMapper.writeValueAsString(object).getBytes(UTF_8.name()));
            } catch (UnsupportedEncodingException | JsonProcessingException exception) {
                throw new IllegalArgumentException(exception.getMessage(), exception);
            }
            return request;
        };
    }

    public static RequestPostProcessor jsonFromClasspath(String file) {
        return request -> {
            request.setContentType(APPLICATION_JSON_UTF8_VALUE);
            try {
                ClassPathResource classPathResource = new ClassPathResource(file);
                request.setContent(IOUtils.toByteArray(classPathResource.getInputStream()));
            } catch (IOException exception) {
                throw new IllegalArgumentException(exception.getMessage(), exception);
            }
            return request;
        };
    }
}
