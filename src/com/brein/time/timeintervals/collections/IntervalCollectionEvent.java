package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.intervals.IInterval;

public class IntervalCollectionEvent {
    private final String key;
    private final IInterval interval;
    private final IntervalCollection collection;
    private final IntervalCollectionEventType eventType;

    public IntervalCollectionEvent(final IInterval interval,
                                   final IntervalCollection collection,
                                   final IntervalCollectionEventType eventType) {
        this(interval.getUniqueIdentifier(), interval, collection, eventType);
    }

    public IntervalCollectionEvent(final String key,
                                   final IInterval interval,
                                   final IntervalCollection collection,
                                   final IntervalCollectionEventType eventType) {
        this.key = key;
        this.interval = interval;
        this.collection = collection;
        this.eventType = eventType;
    }

    public String getKey() {
        return key;
    }

    public IntervalCollection getCollection() {
        return collection;
    }

    public IInterval getInterval() {
        return interval;
    }

    public IntervalCollectionEventType getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return String.format("%s: %s (%s)", eventType, key, interval);
    }
}
