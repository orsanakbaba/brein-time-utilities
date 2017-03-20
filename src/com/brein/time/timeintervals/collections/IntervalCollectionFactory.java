package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.intervals.IInterval;

import java.io.Serializable;

@FunctionalInterface
public interface IntervalCollectionFactory extends  Serializable {

    IntervalCollection load(final IInterval interval);

    /**
     * The generation of a unique key is kind of tricky. It may be
     * that the collection contains several values of different type.
     * We need to generate a unique representation of the interval's start
     * and end value.
     *
     * @param interval the interval to generate the key for
     *
     * @return the unique key to be used
     */
    default String getIntervalKey(final IInterval interval) {
        return interval.getUniqueIdentifier();
    }
}
