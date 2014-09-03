package org.minig.server.resource.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Kamill Sokol
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClientIllegalArgumentException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ClientIllegalArgumentException(String message) {
        super(message);
    }
}
