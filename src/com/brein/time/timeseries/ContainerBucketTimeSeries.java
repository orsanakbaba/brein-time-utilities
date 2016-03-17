package com.brein.time.timeseries;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

public class ContainerBucketTimeSeries<E extends Serializable & Collection<T>, T> extends BucketTimeSeries<E> {
    private static final long serialVersionUID = 1L;

    private final Supplier<E> supplier;

    public ContainerBucketTimeSeries(final Supplier<E> supplier, final BucketTimeSeriesConfig<E> config) {
        super(config);

        this.supplier = supplier;
    }

    public ContainerBucketTimeSeries(final Supplier<E> supplier, final BucketTimeSeriesConfig<E> config, final E[] timeSeries, final long now) {
        super(config, timeSeries, now);

        this.supplier = supplier;
    }

    public void add(final long unixTimeStamp, final T value) {
        final int idx = handleDataUnixTimeStamp(unixTimeStamp);
        add(idx, value);
    }

    public void add(final int idx, final T value) {
        if (idx == -1) {
            return;
        }

        validateIdx(idx);
        getOrCreate(idx).add(value);
    }

    public int size(final int idx) {
        final Collection<T> coll = get(idx);
        return coll == null ? 0 : coll.size();
    }

    @SuppressWarnings("unchecked")
    public Class<E> getCollectionType() {
        return (Class<E>) supplier.get().getClass();
    }

    @SuppressWarnings("unchecked")
    public Class<T> getCollectionContent() {
        final E res = StreamSupport.stream(this.spliterator(), false)
                .filter(coll ->
                        coll != null && coll.stream()
                                .filter(val -> val != null)
                                .findFirst()
                                .orElse(null) != null)
                .findFirst()
                .orElse(null);

        return res == null ? null : (Class<T>) res.stream().findFirst().get().getClass();
    }

    protected E getOrCreate(final int idx) {
        final E coll = get(idx);

        if (coll == null) {
            final E newColl = supplier.get();
            this.timeSeries[idx] = newColl;

            return newColl;
        } else {
            return coll;
        }
    }
}
