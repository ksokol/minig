package org.minig.server.service;

public class RepositoryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RepositoryException() {
	}

	public RepositoryException(String message, Throwable e) {
		super(message, e);
	}

	public RepositoryException(String message) {
		super(message);
	}

}
