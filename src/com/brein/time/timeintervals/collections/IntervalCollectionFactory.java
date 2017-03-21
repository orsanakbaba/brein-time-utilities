package com.brein.time.timeintervals.collections;

import com.brein.time.exceptions.IllegalConfiguration;
import com.brein.time.timeintervals.intervals.IInterval;

import java.io.Serializable;

@FunctionalInterface
public interface IntervalCollectionFactory extends Serializable {

    static IntervalCollection shallow() {
        return ShallowIntervalCollection.SHALLOW_COLLECTION;
    }

    static IntervalCollection shallow(final String key) {
        return shallow();
    }

    IntervalCollection load(final String key);

    default boolean useWeakReferences() {
        return false;
    }

    default void usePersistor(final IntervalCollectionPersistor persistor) {
        if (persistor != null) {
            throw new IllegalConfiguration("The factory does not support the usage of any persistor.");
        }
    }

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
