package com.brein.time.timeintervals.filters;

import com.brein.time.timeintervals.indexes.IntervalValueComparator;
import com.brein.time.timeintervals.intervals.IInterval;

public class IntervalFilters {

    public static boolean weakEqual(final IntervalValueComparator cmp,
                                    final IInterval i1,
                                    final IInterval i2) {
        if (i1.getClass() == i2.getClass()) {
            return i1.equals(i2);
        } else {
            return interval(cmp, i1, i2);
        }
    }

    public static boolean strictEqual(final IntervalValueComparator cmp,
                                      final IInterval i1,
                                      final IInterval i2) {
        if (i1.getClass() == i2.getClass()) {
            return i1.equals(i2);
        } else {
            return false;
        }
    }

    public static boolean equal(final IntervalValueComparator cmp,
                                final IInterval i1,
                                final IInterval i2) {
        return i1.equals(i2);
    }

    public static boolean interval(final IntervalValueComparator cmp,
                                   final IInterval i1,
                                   final IInterval i2) {
        return cmp.compare(i1.getNormStart(), i2.getNormStart()) == 0 &&
                cmp.compare(i1.getNormEnd(), i2.getNormEnd()) == 0;
    }
}
