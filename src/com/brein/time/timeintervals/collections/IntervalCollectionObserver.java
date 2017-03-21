package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.intervals.IInterval;
import org.apache.log4j.Logger;

import java.util.Observable;
import java.util.Observer;

public interface IntervalCollectionObserver extends Observer {
    Logger LOGGER = Logger.getLogger(IntervalCollectionObserver.class);

    void remove(final String key);

    void upsert(final String key, final IntervalCollection coll);

    @Override
    default void update(final Observable o, final Object arg) {
        if (o instanceof ObservableIntervalCollection || o == null) {

            final String key;
            if (arg instanceof IInterval) {
                final IInterval interval = IInterval.class.cast(arg);
                key = interval.getUniqueIdentifier();
            } else if (arg instanceof String) {
                key = String.class.cast(arg);
            } else {
                return;
            }

            final ObservableIntervalCollection coll = ObservableIntervalCollection.class.cast(o);

            if (coll == null) {
                remove(key);
            } else if (coll.isEmpty()) {
                remove(key);
            } else {
                //noinspection unchecked
                upsert(key, coll.getWrappedCollection());
            }
        } else {
            // we just ignore the update
        }
    }
}
