package com.perficient.library.core.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1281346200395559471L;

    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    public UnauthorizedException(String message) {
        super(message);
    }

}
