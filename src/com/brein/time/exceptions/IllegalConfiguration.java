package com.brein.time.exceptions;

public class IllegalConfiguration extends RuntimeException {

    public IllegalConfiguration() {
        super();
    }

    public IllegalConfiguration(final String message) {
        super(message);
    }

    public IllegalConfiguration(final String message, final Throwable cause) {
        super(message, cause);
    }
}
