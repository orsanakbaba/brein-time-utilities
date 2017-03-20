package com.brein.time.timeintervals.collections;

import com.brein.time.timeintervals.intervals.IInterval;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheWriter;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.TimeUnit;

public class CaffeineIntervalCollectionFactory
        implements IntervalCollectionFactory, IntervalCollectionObserver, Externalizable {

    private transient Cache<String, IntervalCollection> cache;
    private transient IntervalCollectionPersistor persistor;

    private long cacheSize;
    private long expire;
    private TimeUnit timeUnit;

    private IntervalCollectionFactory wrappedFactory;

    public CaffeineIntervalCollectionFactory() {
        // just for serialization
    }

    public CaffeineIntervalCollectionFactory(final long cacheSize,
                                             final long expire,
                                             final TimeUnit timeUnit,
                                             final IntervalCollectionFactory wrappedFactory) {
        this.wrappedFactory = wrappedFactory;
        this.cache = createCache(cacheSize, expire, timeUnit);
    }

    protected Cache<String, IntervalCollection> createCache(final long cacheSize,
                                                            final long expire,
                                                            final TimeUnit timeUnit) {
        this.cacheSize = cacheSize;
        this.expire = expire;
        this.timeUnit = timeUnit;

        return Caffeine.newBuilder()
                .maximumSize(cacheSize)
                .expireAfterAccess(expire, timeUnit)
                .writer(new CacheWriter<String, IntervalCollection>() {

                    @Override
                    @SuppressWarnings("NullableProblems")
                    public void write(final String key,
                                      final IntervalCollection coll) {
                        CaffeineIntervalCollectionFactory.this.upsert(key, coll);
                    }

                    @Override
                    @SuppressWarnings("NullableProblems")
                    public void delete(final String key,
                                       final IntervalCollection coll,
                                       final RemovalCause cause) {
                        CaffeineIntervalCollectionFactory.this.remove(key);
                    }
                })
                .build(this::load);
    }

    @Override
    public void remove(final String key) {
        if (this.persistor != null) {
            this.persistor.remove(key);
        }
    }

    @Override
    public void upsert(final String key, final IntervalCollection coll) {
        if (this.persistor != null) {
            this.persistor.upsert(key, coll);
        }
    }

    public IntervalCollection load(final String key) {
        if (this.persistor == null) {
            return null;
        } else {
            return this.persistor.load(key);
        }
    }

    @Override
    public IntervalCollection load(final IInterval interval) {
        final IntervalCollection result;

        if (this.persistor == null) {
            result = null;
        } else {
            result = load(interval.getUniqueIdentifier());
        }

        if (result == null) {
            return this.wrappedFactory.load(interval);
        } else {
            return result;
        }
    }

    public long size() {
        return this.cache.estimatedSize();
    }

    public void clear() {
        this.cache.cleanUp();
    }

    public void setPersistor(final IntervalCollectionPersistor persistor) {
        this.persistor = persistor;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        if (this.persistor != null && LOGGER.isInfoEnabled()) {
            LOGGER.info("Please make sure that the persistor '" + this.persistor.getClass() + "' " +
                    "will be re-initialized prior to using the persisted instance.");
        }

        out.writeLong(this.cacheSize);
        out.writeLong(this.expire);
        out.writeObject(this.timeUnit);

        out.writeObject(this.wrappedFactory);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final long cacheSize = in.readLong();
        final long expire = in.readLong();
        final TimeUnit timeUnit = TimeUnit.class.cast(in.readObject());
        this.cache = createCache(cacheSize, expire, timeUnit);

        this.wrappedFactory = IntervalCollectionFactory.class.cast(in.readObject());
    }
}
