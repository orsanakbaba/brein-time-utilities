package com.brein.time.timeseries;

import com.brein.time.exceptions.IllegalBucketEndPoints;
import com.brein.time.utils.TimeUtils;

import java.util.Objects;

/**
 * The {@code BucketEndPoints} represents the end-points of a bucket, which
 * is used by an {@code BucketTimeSeries}. In mathematics it would be represented
 * as an interval, which includes the start and excludes the end, i.e.,
 * [{@code start}, {@code unixTimeStampEnd}).
 *
 * @author Philipp Meisen
 */
public class BucketEndPoints implements Comparable<BucketEndPoints> {
    private final long start;
    private final long end;

    /**
     * Creates a {@code BucketEndPoints} instance.
     *
     * @param unixTimeStampStart the start endpoint (inclusive)
     * @param unixTimeStampEnd   the end endpoint (exclusive)
     * @throws IllegalBucketEndPoints if the endpoints are invalid, i.e.,
     *                                if the end is smaller or eqaul to the start
     */
    public BucketEndPoints(final long unixTimeStampStart, final long unixTimeStampEnd) throws IllegalBucketEndPoints {
        if (unixTimeStampEnd <= unixTimeStampStart) {
            throw new IllegalBucketEndPoints(String.format("The defined boundaries are invalid: %d (end) <= %d (start)", unixTimeStampEnd, unixTimeStampStart));
        }

        this.start = unixTimeStampStart;
        this.end = unixTimeStampEnd;
    }

    public long getUnixTimeStampEnd() {
        return end;
    }

    public long getUnixTimeStampStart() {
        return start;
    }

    public long size() {
        return end - start;
    }

    public long diff(final BucketEndPoints other) {
        final long size = size();

        /*
         * The first part of this logic is obvious, the size must be equal
         * otherwise, the difference cannot be determined (i.e., the data
         * is used in different BucketTimeSeries).
         *
         * The second part checks if the bucketing of the time axis is the
         * same. If not to calculate the difference between buckets makes
         * no sense.
         */
        if (size != other.size() || (start - other.start) % size != 0) {
            throw new IllegalBucketEndPoints(String.format("The buckets %s and %s do not belong to the same configuration and cannot be compared, or better the difference between these bucket end points cannot be determined.", this, other));
        }

        return (other.start - start) / size;
    }

    public BucketEndPoints move(final long buckets) {
        if (buckets == 0) {
            return this;
        } else {
            final long forward = buckets * size();
            return new BucketEndPoints(start + forward, end + forward);
        }
    }

    @Override
    public String toString() {
        return String.format("[%d, %d) == [%s, %s)", start, end, TimeUtils.format(start), TimeUtils.format(end));
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (BucketEndPoints.class.isInstance(obj)) {
            final BucketEndPoints other = BucketEndPoints.class.cast(obj);

            return start == other.start && end == other.end;
        } else {
            return false;
        }
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(final BucketEndPoints other) {
        int res = Long.compare(start, other.start);
        if (res == 0) {
            res = Long.compare(end, other.end);
        }

        return res;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Long.hashCode(start), Long.hashCode(end));
    }
}
