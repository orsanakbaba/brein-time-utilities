package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.intervals.Interval;

import java.util.Collection;
import java.util.stream.Collectors;

public interface IntervalCollection extends Collection<Interval> {

    @FunctionalInterface
    interface IntervalFilter {
        boolean match(final Interval i1, final Interval i2);
    }

    class IntervalFilters {
        public static final IntervalFilter EQUAL = Interval::equals;
        
        public static final IntervalFilter INTERVAL =
                (i1, i2) -> i1.getNormStart() == i2.getNormStart() &&
                        i1.getNormEnd() == i2.getNormEnd();

        public static final IntervalFilter STRICT_EQUAL = (i1, i2) -> {
            if (i1.getClass() == i2.getClass()) {
                return i1.equals(i2);
            } else {
                return INTERVAL.match(i1, i2);
            }
        };
    }

    default Collection<Interval> find(final Interval interval) {
        return find(interval, IntervalFilters.EQUAL);
    }

    default Collection<Interval> find(final Interval interval, final IntervalFilter filter) {
        return stream()
                .filter(i -> filter.match(i, interval))
                .collect(Collectors.toList());
    }
}
