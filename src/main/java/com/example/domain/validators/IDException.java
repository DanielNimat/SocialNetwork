package com.example.domain.validators;

public class IDException extends RuntimeException {
    public IDException() {
    }

    public IDException(String message) {
        super(message);
    }

    public IDException(String message, Throwable cause) {
        super(message, cause);
    }

    public IDException(Throwable cause) {
        super(cause);
    }

    public IDException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}