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

        final Collection<T> coll = getOrCreate(idx);
        coll.add(value);
    }

    protected Collection<T> getOrCreate(final int idx) {
        final Collection<T> coll = this.timeSeries[idx];

        if (coll == null) {
            final Collection<T> newColl = supplier.get();
            this.timeSeries[idx] = newColl;

            return newColl;
        } else {
            return coll;
        }
    }
}
