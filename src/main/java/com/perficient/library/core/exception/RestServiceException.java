package com.perficient.library.core.exception;

public class RestServiceException extends RuntimeException {

    private static final long serialVersionUID = 6226179756554976359L;

    public RestServiceException() {

    }

    public RestServiceException(String message) {
        super(message);
    }
}
