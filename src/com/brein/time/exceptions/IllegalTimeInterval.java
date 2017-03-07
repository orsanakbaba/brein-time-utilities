package com.brein.time.exceptions;

public class IllegalTimeInterval extends RuntimeException {

    public IllegalTimeInterval() {
        super();
    }

    public IllegalTimeInterval(final String message) {
        super(message);
    }

    public IllegalTimeInterval(final String message, final Throwable cause) {
        super(message, cause);
    }
}
