package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.intervals.IInterval;

import java.util.Collection;
import java.util.stream.Collectors;

public interface IntervalCollection extends Collection<IInterval> {

    @FunctionalInterface
    interface IntervalFilter {
        boolean match(final IInterval i1, final IInterval i2);
    }

    class IntervalFilters {
        public static final IntervalFilter EQUAL = IInterval::equals;

        @SuppressWarnings("unchecked")
        public static final IntervalFilter INTERVAL =
                (i1, i2) -> i1.getNormStart().equals(i2.getNormStart()) &&
                        i1.getNormEnd().equals(i2.getNormEnd());

        public static final IntervalFilter STRICT_EQUAL = (i1, i2) -> {
            if (i1.getClass() == i2.getClass()) {
                return i1.equals(i2);
            } else {
                return INTERVAL.match(i1, i2);
            }
        };
    }

    default Collection<IInterval> find(final IInterval interval) {
        return find(interval, IntervalFilters.EQUAL);
    }

    default Collection<IInterval> find(final IInterval interval, final IntervalFilter filter) {
        return stream()
                .filter(i -> filter.match(i, interval))
                .collect(Collectors.toList());
    }
}
