package org.minig.server.service;

public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 7418464342609824825L;

    public ServiceException(String message) {
        super(message);
    }

}
