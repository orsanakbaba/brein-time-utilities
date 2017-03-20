package com.brein.time.timeintervals.intervals;

import java.io.Externalizable;
import java.io.Serializable;

public interface IInterval<T extends Comparable<T> & Serializable>
        extends Comparable<IInterval>, Cloneable, Serializable, Externalizable {

    /**
     * Gets the first included value in this interval.
     *
     * @return the first included value in this interval
     */
    T getNormStart();

    /**
     * Gets the last included value in this interval.
     *
     * @return the last included value in this interval
     */
    T getNormEnd();

    IntervalComparator getComparator();

    /**
     * Method to uniquely identify the interval of this instance. The term interval means in this specific case, just
     * the mathematical interpretation of an interval, i.e., [norm-start, norm-end]. The returned value must be unique
     * across all the intervals, representing the same range, within the implemented domain, e.g., [5.0, 6.0] == [5, 6].
     *
     * @return the unique identifier
     */
    String getUniqueIdentifier();
}
