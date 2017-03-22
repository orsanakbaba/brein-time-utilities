package com.brein.time.timeintervals.collections;

import org.apache.log4j.Logger;

import java.util.Observable;
import java.util.Observer;

public interface IntervalCollectionObserver extends Observer {
    Logger LOGGER = Logger.getLogger(IntervalCollectionObserver.class);

    void remove(final IntervalCollectionEvent event);

    void upsert(final IntervalCollectionEvent event);

    @Override
    default void update(final Observable o, final Object arg) {
        if (o instanceof ObservableIntervalCollection) {
            final ObservableIntervalCollection coll = ObservableIntervalCollection.class.cast(o);

            if (arg instanceof IntervalCollectionEvent) {
                final IntervalCollectionEvent event = IntervalCollectionEvent.class.cast(arg);

                switch (event.getEventType()) {
                    case REMOVED:
                        remove(event);
                        break;
                    case ADDED:
                        upsert(event);
                        break;
                }
            }
        }
    }
}
