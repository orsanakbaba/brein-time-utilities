package com.brein.time.timeintervals.intervals;

import com.brein.time.exceptions.IllegalTimeInterval;
import com.brein.time.exceptions.IllegalTimePoint;

public class LongInterval extends NumberInterval<Long> {

    public LongInterval() {
        super();
    }

    public LongInterval(final Long start, final Long end) throws IllegalTimeInterval, IllegalTimePoint {
        this(start, end, false, false);
    }

    public LongInterval(final Long start, final Long end, final boolean openStart, final boolean openEnd)
            throws IllegalTimeInterval, IllegalTimePoint {
        super(Long.class, start, end, openStart, openEnd);
    }
}
