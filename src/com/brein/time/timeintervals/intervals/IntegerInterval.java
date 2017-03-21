package com.brein.time.timeintervals.intervals;

import com.brein.time.exceptions.IllegalTimeInterval;
import com.brein.time.exceptions.IllegalTimePoint;

public class IntegerInterval extends NumberInterval<Integer> {

    public IntegerInterval() {
        super();
    }

    public IntegerInterval(final Integer start, final Integer end) throws IllegalTimeInterval, IllegalTimePoint {
        this(start, end, false, false);
    }

    public IntegerInterval(final Integer start, final Integer end, final boolean openStart, final boolean openEnd)
            throws IllegalTimeInterval, IllegalTimePoint {
        super(Integer.class, start, end, openStart, openEnd);
    }
}
