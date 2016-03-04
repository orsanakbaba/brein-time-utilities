package com.brein.time.exceptions;

public class IllegalTimePoint extends RuntimeException {

    public IllegalTimePoint() {
        super();
    }

    public IllegalTimePoint(final String message) {
        super(message);
    }

    public IllegalTimePoint(final String message, final Throwable cause) {
        super(message, cause);
    }
}
