package com.brein.time.exceptions;

public class FailedLoad extends RuntimeException {

    public FailedLoad() {
        super();
    }

    public FailedLoad(final String message) {
        super(message);
    }

    public FailedLoad(final String message, final Throwable cause) {
        super(message, cause);
    }
}

