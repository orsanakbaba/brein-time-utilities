package com.brein.time.timeseries;

import java.util.Collection;
import java.util.function.Supplier;

public class ContainerBucketTimeSeries<T> extends BucketTimeSeries<Collection<T>> {
    private final Supplier<Collection<T>> supplier;

    public ContainerBucketTimeSeries(final Supplier<Collection<T>> supplier, final BucketTimeSeriesConfig<Collection<T>> config) {
        super(config);

        this.supplier = supplier;
    }

    public void add(final int idx, final T value) {
        validateIdx(idx);
        getOrCreate(idx).add(value);
    }

    public int size(final int idx) {
        final Collection<T> coll = get(idx);
        return coll == null ? 0 : coll.size();
    }

    protected Collection<T> getOrCreate(final int idx) {
        final Collection<T> coll = get(idx);

        if (coll == null) {
            final Collection<T> newColl = supplier.get();
            this.timeSeries[idx] = newColl;

            return newColl;
        } else {
            return coll;
        }
    }
}
