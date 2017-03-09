package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.intervals.Interval;

import java.util.Collection;
import java.util.stream.Collectors;

public interface IntervalCollection extends Collection<Interval> {

    default Collection<Interval> find(final Interval interval) {
        return stream()
                .filter(i -> i.equals(interval))
                .collect(Collectors.toList());
    }
}
