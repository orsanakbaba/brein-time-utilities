package com.brein.time.exceptions;

public class IllegalTimePointMovement extends RuntimeException {

    public IllegalTimePointMovement() {
        super();
    }

    public IllegalTimePointMovement(final String message) {
        super(message);
    }

    public IllegalTimePointMovement(final String message, final Throwable cause) {
        super(message, cause);
    }
}
