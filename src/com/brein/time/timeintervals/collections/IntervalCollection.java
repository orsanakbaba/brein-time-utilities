package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.filters.IntervalFilter;
import com.brein.time.timeintervals.filters.IntervalFilters;
import com.brein.time.timeintervals.indexes.IntervalValueComparator;
import com.brein.time.timeintervals.intervals.IInterval;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface IntervalCollection extends Iterable<IInterval> {

    boolean add(final IInterval interval);

    boolean remove(final IInterval interval);

    boolean isEmpty();

    int size();

    default Collection<IInterval> find(final IInterval interval, final IntervalValueComparator cmp) {
        return find(interval, cmp, IntervalFilters::equal);
    }

    default Collection<IInterval> find(final IInterval interval,
                                       final IntervalValueComparator cmp,
                                       final IntervalFilter filter) {
        return stream()
                .filter(i -> filter.match(cmp, i, interval))
                .collect(Collectors.toList());
    }

    default Stream<IInterval> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Each interval has a unique identifier. It is by design, that all elements within on collection have the same
     * {@link IInterval#getUniqueIdentifier()}. This method can be used to determine this unique identifier of all the
     * intervals within {@code this} collection. If the collection does not contain any interval, the method returns
     * {@code null}.
     *
     * @return the unique identifier of all the intervals within the collection
     */
    default String getUniqueIdentifier() {
        if (isEmpty()) {
            return null;
        }

        final IInterval interval = iterator().next();
        return interval.getUniqueIdentifier();
    }
}
