package com.brein.time.exceptions;

public class FailedIO extends RuntimeException {

    public FailedIO() {
        super();
    }

    public FailedIO(final String message) {
        super(message);
    }

    public FailedIO(final String message, final Throwable cause) {
        super(message, cause);
    }
}
