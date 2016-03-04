package com.brein.time.exceptions;

public class IllegalTimePointIndex extends IllegalTimePoint {

    public IllegalTimePointIndex() {
        super();
    }

    public IllegalTimePointIndex(final String message) {
        super(message);
    }

    public IllegalTimePointIndex(final String message, final Throwable cause) {
        super(message, cause);
    }
}
