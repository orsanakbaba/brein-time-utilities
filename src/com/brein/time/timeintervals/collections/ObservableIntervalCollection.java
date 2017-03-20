package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.intervals.IInterval;
import org.apache.log4j.Logger;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Observable;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObservableIntervalCollection extends Observable implements IntervalCollection, Externalizable {
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
    public Collection<IInterval> find(final IInterval interval) {
        return this.collection.find(interval);
    }

    @Override
    public Collection<IInterval> find(final IInterval interval, final IntervalFilter filter) {
        return this.collection.find(interval, filter);
    }

    @Override
    public int size() {
        return this.collection.size();
    }

    @Override
    public boolean isEmpty() {
        return this.collection.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return this.collection.contains(o);
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
    @SuppressWarnings("NullableProblems")
    public Object[] toArray() {
        return this.collection.toArray();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public <T> T[] toArray(final T[] a) {
        return this.collection.toArray(a);
    }

    @Override
    public boolean add(final IInterval interval) {
        return notifyObservers(interval.getUniqueIdentifier(), this.collection.add(interval));
    }

    @Override
    public boolean remove(final Object o) {
        final boolean result = this.collection.remove(o);
        if (result) {
            this.notifyObservers(o);
        }

        return result;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean containsAll(final Collection<?> c) {
        return this.collection.containsAll(c);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean addAll(final Collection<? extends IInterval> c) {
        this.disableNotification.set(true);
        final boolean result = this.collection.addAll(c);
        this.disableNotification.set(false);

        if (result) {
            notifyObservers(getUniqueIdentifier(), true);
        }

        return result;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean removeAll(final Collection<?> c) {
        final String key = getUniqueIdentifier();

        this.disableNotification.set(true);
        final boolean result = this.collection.removeAll(c);
        this.disableNotification.set(false);

        if (result) {
            notifyObservers(key, true);
        }

        return result;

    }

    @Override
    public boolean removeIf(final Predicate<? super IInterval> filter) {
        return notifyObservers(getUniqueIdentifier(), this.collection.removeIf(filter));
    }

    public boolean notifyObservers(final String key, final boolean result) {
        if (key == null || this.countObservers() == 0 || !result) {
            return false;
        } else if (!disableNotification.get()) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Notifying observers for '" + key + "'.");
            }

            // mark a change
            setChanged();

            // notify
            super.notifyObservers(key);
        }

        return true;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean retainAll(final Collection<?> c) {
        final String key = getUniqueIdentifier();

        this.disableNotification.set(true);
        final boolean result = this.collection.retainAll(c);
        this.disableNotification.set(false);

        if (result) {
            notifyObservers(key, true);
        }

        return result;
    }

    @Override
    public void clear() {
        if (isEmpty()) {
            return;
        }

        final String key = getUniqueIdentifier();

        this.disableNotification.set(true);
        this.collection.clear();
        this.disableNotification.set(false);

        notifyObservers(key, true);
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
    public int hashCode() {
        return this.collection.hashCode();
    }

    @Override
    public Spliterator<IInterval> spliterator() {
        return this.collection.spliterator();
    }

    @Override
    public Stream<IInterval> stream() {
        return this.collection.stream();
    }

    @Override
    public Stream<IInterval> parallelStream() {
        return this.collection.parallelStream();
    }

    @Override
    public void forEach(final Consumer<? super IInterval> action) {
        this.collection.forEach(action);
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(this.collection);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.collection = IntervalCollection.class.cast(in.readObject());
    }

    @Override
    public String toString() {
        return this.collection.toString();
    }
}
