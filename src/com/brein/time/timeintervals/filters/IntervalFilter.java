package com.brein.time.timeintervals.filters;

import com.brein.time.timeintervals.indexes.IntervalValueComparator;
import com.brein.time.timeintervals.intervals.IInterval;

import java.io.Serializable;

@FunctionalInterface
public interface IntervalFilter extends Serializable {
    boolean match(final IntervalValueComparator comparator, final IInterval i1, final IInterval i2);
}
