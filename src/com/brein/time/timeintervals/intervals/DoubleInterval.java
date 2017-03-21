package com.brein.time.timeintervals.intervals;

import com.brein.time.exceptions.IllegalTimeInterval;
import com.brein.time.exceptions.IllegalTimePoint;

public class DoubleInterval extends NumberInterval<Double> {

    public DoubleInterval() {
        super();
    }

    public DoubleInterval(final Double start, final Double end) throws IllegalTimeInterval, IllegalTimePoint {
        this(start, end, false, false);
    }

    public DoubleInterval(final Double start, final Double end, final boolean openStart, final boolean openEnd)
            throws IllegalTimeInterval, IllegalTimePoint {
        super(Double.class, start, end, openStart, openEnd);
    }
}
