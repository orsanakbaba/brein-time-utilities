package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.filters.IntervalFilter;
import com.brein.time.timeintervals.indexes.IntervalValueComparator;
import com.brein.time.timeintervals.intervals.IInterval;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

public class UnmodifiableIntervalCollection implements IntervalCollection {

    private transient final IntervalCollection wrappedCollection;

    public UnmodifiableIntervalCollection(final IntervalCollection wrappedCollection) {
        this.wrappedCollection = wrappedCollection;
    }

    @Override
    public boolean add(final IInterval interval) {
        throw new IllegalStateException("Collection is unmodifiable.");
    }

    @Override
    public boolean remove(final IInterval interval) {
        throw new IllegalStateException("Collection is unmodifiable.");
    }

    @Override
    public Iterator<IInterval> iterator() {
        final Iterator<IInterval> it = wrappedCollection.iterator();
        return new Iterator<IInterval>() {

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public IInterval next() {
                return it.next();
            }
        };
    }

    @Override
    public boolean isEmpty() {
        return wrappedCollection.isEmpty();
    }

    @Override
    public int size() {
        return wrappedCollection.size();
    }

    @Override
    public Collection<IInterval> find(final IInterval interval, final IntervalValueComparator cmp) {
        return wrappedCollection.find(interval, cmp);
    }

    @Override
    public Collection<IInterval> find(final IInterval interval,
                                      final IntervalValueComparator cmp,
                                      final IntervalFilter filter) {
        return wrappedCollection.find(interval, cmp, filter);
    }

    @Override
    public Stream<IInterval> stream() {
        return wrappedCollection.stream();
    }

    @Override
    public String getUniqueIdentifier() {
        return wrappedCollection.getUniqueIdentifier();
    }
}
