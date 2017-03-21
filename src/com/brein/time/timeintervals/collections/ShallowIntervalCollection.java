package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.filters.IntervalFilter;
import com.brein.time.timeintervals.indexes.IntervalValueComparator;
import com.brein.time.timeintervals.intervals.IInterval;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class ShallowIntervalCollection implements IntervalCollection {
    public static final ShallowIntervalCollection SHALLOW_COLLECTION = new ShallowIntervalCollection();

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Iterator<IInterval> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public boolean add(final IInterval iInterval) {
        return false;
    }

    @Override
    public boolean remove(final IInterval o) {
        return false;
    }

    @Override
    public Collection<IInterval> find(final IInterval interval,
                                      final IntervalValueComparator cmp) {
        return find(interval, cmp, null);
    }

    @Override
    public Collection<IInterval> find(final IInterval interval,
                                      final IntervalValueComparator cmp,
                                      final IntervalFilter filter) {
        return Collections.singletonList(interval);
    }

    /**
     * Method used to ensure that when de-serialized we use the one and only instance.
     *
     * @return the resolved shallow collection
     */
    private Object readResolve() {
        return SHALLOW_COLLECTION;
    }
}
