package com.brein.time.exceptions;

public class IllegalBucketEndPoints extends RuntimeException {

    public IllegalBucketEndPoints() {
        super();
    }

    public IllegalBucketEndPoints(final String message) {
        super(message);
    }

    public IllegalBucketEndPoints(final String message, final Throwable cause) {
        super(message, cause);
    }
}
