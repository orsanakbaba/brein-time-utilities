package com.brein.time.timeintervals.collections;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.TimeUnit;

public class CaffeineIntervalCollectionFactory extends PersistableIntervalCollectionFactory implements Externalizable {

    private transient LoadingCache<String, IntervalCollection> cache;

    private long cacheSize;
    private long expire;
    private TimeUnit timeUnit;

    public CaffeineIntervalCollectionFactory() {
        // just for serialization
    }

    public CaffeineIntervalCollectionFactory(final long cacheSize,
                                             final long expire,
                                             final TimeUnit timeUnit,
                                             final IntervalCollectionFactory wrappedFactory) {
        super(wrappedFactory);
        this.cache = createCache(cacheSize, expire, timeUnit);
    }

    protected LoadingCache<String, IntervalCollection> createCache(final long cacheSize,
                                                                   final long expire,
                                                                   final TimeUnit timeUnit) {
        this.cacheSize = cacheSize;
        this.expire = expire;
        this.timeUnit = timeUnit;

        return Caffeine.newBuilder()
                .maximumSize(cacheSize)
                .expireAfterAccess(expire, timeUnit)
                .build(super::load);
    }

    @Override
    public IntervalCollection load(final String key) {
        final IntervalCollection result = this.cache.get(key);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Loading instance from cache '" + key + "': " + result);
        }

        return result;
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeLong(this.cacheSize);
        out.writeLong(this.expire);
        out.writeObject(this.timeUnit);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        final long cacheSize = in.readLong();
        final long expire = in.readLong();
        final TimeUnit timeUnit = TimeUnit.class.cast(in.readObject());
        this.cache = createCache(cacheSize, expire, timeUnit);
    }

    public long size() {
        return this.cache.estimatedSize();
    }

    public void clear() {
        this.cache.cleanUp();
    }
}
