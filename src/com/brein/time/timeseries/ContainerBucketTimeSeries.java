package com.brein.time.timeseries;

import com.brein.time.exceptions.IllegalConfiguration;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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

    public long[] createByContent(final ContainerBucketTimeSeries<E, T> timeSeries, final BiFunction<E, E, Long> create) {
        final ContainerBucketTimeSeries<E, T> syncedTs = sync(timeSeries, (ts) -> new ContainerBucketTimeSeries<>(ts.getSupplier(), ts.getConfig(), ts.timeSeries, ts.getNow()));

        final long[] result = new long[config.getTimeSeriesSize()];
        for (int i = 0; i < config.getTimeSeriesSize(); i++) {
            final int idx = idx(currentNowIdx + i);

            final E coll = get(idx);
            final E syncedColl = syncedTs.get(syncedTs.idx(syncedTs.currentNowIdx + i));

            final Long val = create.apply(coll, syncedColl);
            result[i] = val == null ? -1L : val;
        }

        return result;
    }

    public void combineByContent(final ContainerBucketTimeSeries<E, T> timeSeries, final BiConsumer<E, E> combine) throws IllegalConfiguration {
        combineByContent(timeSeries, (coll1, coll2) -> {

            final E in1, in2;
            if (coll1 == null && coll2 == null) {
                in1 = getSupplier().get();
                in2 = getSupplier().get();
            } else if (coll1 == null) {
                in1 = getSupplier().get();
                in2 = coll2;
            } else if (coll2 == null) {
                in1 = coll1;
                in2 = getSupplier().get();
            } else {
                in1 = coll1;
                in2 = coll2;
            }

            combine.accept(in1, in2);
            return in1;
        });
    }

    public void combineByContent(final ContainerBucketTimeSeries<E, T> timeSeries, final BiFunction<E, E, E> combine) throws IllegalConfiguration {
        final ContainerBucketTimeSeries<E, T> syncedTs = sync(timeSeries, (ts) -> new ContainerBucketTimeSeries<>(ts.getSupplier(), ts.getConfig(), ts.timeSeries, ts.getNow()));

        for (int i = 0; i < config.getTimeSeriesSize(); i++) {
            final int idx = idx(currentNowIdx + i);

            final E coll = get(idx);
            final E syncedColl = syncedTs.get(syncedTs.idx(syncedTs.currentNowIdx + i));
            final E result = combine.apply(coll, syncedColl);

            // let's see if the collection changed, if so we set it
            if (coll != result) {
                set(idx, result);
            }
        }
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

    public Supplier<E> getSupplier() {
        return supplier;
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
