package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.intervals.IInterval;

import java.util.ArrayList;
import java.util.stream.Stream;

public class ListIntervalCollection extends ArrayList<IInterval> implements IntervalCollection {

    @Override
    public boolean remove(final IInterval interval) {
        return super.remove(interval);
    }

    @Override
    public Stream<IInterval> stream() {
        return IntervalCollection.super.stream();
    }
}
