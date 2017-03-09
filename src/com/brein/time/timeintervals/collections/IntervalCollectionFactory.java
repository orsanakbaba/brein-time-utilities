package com.brein.time.timeintervals.collections;

import java.util.function.Supplier;

public interface IntervalCollectionFactory<T extends IntervalCollection> extends Supplier<IntervalCollection> {

    @Override
    IntervalCollection get();
}
