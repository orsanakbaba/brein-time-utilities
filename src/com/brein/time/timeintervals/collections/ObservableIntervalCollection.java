package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.filters.IntervalFilter;
import com.brein.time.timeintervals.indexes.IntervalValueComparator;
import com.brein.time.timeintervals.intervals.IInterval;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class ObservableIntervalCollection extends Observable implements IntervalCollection {
    private static final Logger LOGGER = Logger.getLogger(ObservableIntervalCollection.class);

    private final transient AtomicBoolean disableNotification = new AtomicBoolean(false);
    private IntervalCollection collection;

    public ObservableIntervalCollection() {
        // just for serialization
    }

    public ObservableIntervalCollection(final IntervalCollectionObserver observer,
                                        final IntervalCollection collection) {
        addObserver(observer);
        this.collection = collection;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Iterator<IInterval> iterator() {

        // make sure remove is not supported
        final Iterator<IInterval> it = this.collection.iterator();
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
    public boolean add(final IInterval interval) {
        return notifyObservers(interval, IntervalCollectionEventType.UPSERTED, this.collection.add(interval));
    }

    @Override
    public boolean remove(final IInterval interval) {
        return notifyObservers(interval, IntervalCollectionEventType.REMOVED, this.collection.remove(interval));
    }

    @Override
    public boolean isEmpty() {
        return this.collection.isEmpty();
    }

    @Override
    public int size() {
        return this.collection.size();
    }

    @Override
    public Collection<IInterval> find(final IInterval interval, final IntervalValueComparator cmp) {
        return this.collection.find(interval, cmp);
    }

    @Override
    public Collection<IInterval> find(final IInterval interval,
                                      final IntervalValueComparator cmp,
                                      final IntervalFilter filter) {
        return this.collection.find(interval, cmp, filter);
    }

    @Override
    public Stream<IInterval> stream() {
        return this.collection.stream();
    }

    public boolean notifyObservers(final IInterval interval,
                                   final IntervalCollectionEventType eventType,
                                   final boolean result) {
        if (interval == null || this.countObservers() == 0 || !result) {
            return false;
        } else if (!disableNotification.get()) {
            final String key = interval.getUniqueIdentifier();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Notifying observers for '" + key + "'.");
            }

            // mark a change
            setChanged();

            // notify
            final IntervalCollectionEvent event =
                    new IntervalCollectionEvent(key, interval, this.getWrappedCollection(), eventType);
            super.notifyObservers(event);
        }

        return true;
    }

    @Override
    public int hashCode() {
        return this.collection.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (ObservableIntervalCollection.class.equals(obj.getClass())) {
            final ObservableIntervalCollection coll = ObservableIntervalCollection.class.cast(obj);
            return Objects.equals(this.collection, coll.collection);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.collection.toString();
    }

    public IntervalCollection getWrappedCollection() {
        return this.collection;
    }
}
