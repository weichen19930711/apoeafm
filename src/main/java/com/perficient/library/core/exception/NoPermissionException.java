package com.perficient.library.core.exception;

import org.springframework.http.HttpStatus;

public class NoPermissionException extends RuntimeException {

    private static final long serialVersionUID = 5978546369705754641L;

    public NoPermissionException() {
        super(HttpStatus.FORBIDDEN.getReasonPhrase());
    }

    public NoPermissionException(String message) {
        super(message);
    }

}
