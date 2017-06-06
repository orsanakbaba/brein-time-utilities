package com.brein.time.exceptions;

public class FailedConnection extends RuntimeException {

    public FailedConnection() {
        super();
    }

    public FailedConnection(final String message) {
        super(message);
    }

    public FailedConnection(final String message, final Throwable cause) {
        super(message, cause);
    }
}

