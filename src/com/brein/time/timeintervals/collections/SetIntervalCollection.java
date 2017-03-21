package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.intervals.IInterval;

import java.util.HashSet;
import java.util.stream.Stream;

public class SetIntervalCollection extends HashSet<IInterval> implements IntervalCollection {

    @Override
    public boolean remove(final IInterval interval) {
        return super.remove(interval);
    }

    @Override
    public Stream<IInterval> stream() {
        return IntervalCollection.super.stream();
    }
}
