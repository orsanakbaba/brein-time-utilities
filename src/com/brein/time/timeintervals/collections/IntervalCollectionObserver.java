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
            if (arg instanceof IntervalCollectionEvent) {
                final IntervalCollectionEvent event = IntervalCollectionEvent.class.cast(arg);

                if (IntervalCollectionEventType.REMOVED == event.getEventType()) {
                    remove(event);
                } else if (IntervalCollectionEventType.UPSERTED == event.getEventType()) {
                    upsert(event);
                }
            }
        }
    }
}
