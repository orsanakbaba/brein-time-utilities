package com.brein.time.timeintervals.collections;

public interface IntervalCollectionPersistor {

    IntervalCollection load(final String key);

    void upsert(final String key, final IntervalCollection collection);

    void remove(final String key);
}
