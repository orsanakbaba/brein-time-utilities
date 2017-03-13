package com.brein.time.timeintervals.intervals;

import java.util.Comparator;

public interface IInterval<T extends Comparable<T>> extends Comparable<IInterval>, Cloneable {

    T getNormStart();

    T getNormEnd();

    Comparator getComparator();
}
