package com.brein.time.timeintervals.collections;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class PersistableIntervalCollectionFactory
        implements IntervalCollectionFactory, IntervalCollectionObserver, Externalizable {

    private transient IntervalCollectionPersistor persistor;

    private IntervalCollectionFactory wrappedFactory;

    public PersistableIntervalCollectionFactory() {
        // just for serialization
    }

    public PersistableIntervalCollectionFactory(final IntervalCollectionFactory wrappedFactory) {
        this.wrappedFactory = wrappedFactory;
    }

    @Override
    public void remove(final IntervalCollectionEvent event) {
        if (this.persistor != null) {
            this.persistor.remove(event);
        }
    }

    @Override
    public void upsert(final IntervalCollectionEvent event) {
        if (this.persistor != null) {
            this.persistor.upsert(event);
        }
    }

    @Override
    public IntervalCollection load(final String key) {
        IntervalCollection result;
        if (this.persistor == null) {
            result = null;
        } else {
            result = this.persistor.load(key);
        }

        if (result == null) {
            result = this.wrappedFactory.load(key);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Using created collection for '" + key + "': " + result);
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Using persisted collection for '" + key + "': " + result);
            }
        }

        return result;
    }

    @Override
    public boolean useWeakReferences() {
        return true;
    }

    @Override
    public void usePersistor(final IntervalCollectionPersistor persistor) {
        this.persistor = persistor;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        if (this.persistor != null && LOGGER.isInfoEnabled()) {
            LOGGER.info("Please make sure that the persistor '" + this.persistor.getClass() + "' " +
                    "will be re-initialized prior to using the persisted instance.");
        }

        out.writeObject(this.wrappedFactory);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.wrappedFactory = IntervalCollectionFactory.class.cast(in.readObject());
    }
}
