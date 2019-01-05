package com.perficient.library.core.exception;


public class ValidationFailedException extends RuntimeException {

    private static final long serialVersionUID = -7611880672800561268L;

    public ValidationFailedException() {

    }

    public ValidationFailedException(String message) {
        super(message);
    }
}
