package com.brein.time.timeintervals.collections;

public interface IntervalCollectionPersistor {

    IntervalCollection load(final String key);

    void upsert(final IntervalCollectionEvent event);

    void remove(final IntervalCollectionEvent event);
}
