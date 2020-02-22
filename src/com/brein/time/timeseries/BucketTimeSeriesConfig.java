package com.brein.time.timeseries;

import com.brein.time.timeintervals.intervals.IntegerInterval;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BucketTimeSeriesConfig<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(BucketTimeSeriesConfig.class);
    private static final Set<TimeUnit> SUPPORTED_TIME_UNITS = new HashSet<>();

    static {
        SUPPORTED_TIME_UNITS.add(TimeUnit.SECONDS);
        SUPPORTED_TIME_UNITS.add(TimeUnit.MINUTES);
        SUPPORTED_TIME_UNITS.add(TimeUnit.HOURS);
    }

    private TimeUnit timeUnit =  TimeUnit.SECONDS;
    private  int bucketSize = 1;
    private  int timeSeriesSize = 100;
    private  Class<?> bucketContent = IntegerInterval.class;
    private  boolean fillNumberWithZero = true;


    public BucketTimeSeriesConfig() {
    }

    public BucketTimeSeriesConfig(final Class<?> bucketContent, final TimeUnit timeUnit, final int timeSeriesSize) {
        this(bucketContent, timeUnit, timeSeriesSize, 1);
    }

    public BucketTimeSeriesConfig(final Class<?> bucketContent, final TimeUnit timeUnit, final int timeSeriesSize,
                                  final int bucketSize) {
        this(bucketContent, timeUnit, timeSeriesSize, bucketSize, Number.class.isAssignableFrom(bucketContent));
    }

    public BucketTimeSeriesConfig(final Class<?> bucketContent, final TimeUnit timeUnit, final int timeSeriesSize,
                                  final int bucketSize, final boolean fillNumberWithZero) {
        this.timeUnit = timeUnit;
        this.timeSeriesSize = timeSeriesSize;
        this.bucketSize = bucketSize;
        this.bucketContent = bucketContent;
        this.fillNumberWithZero = fillNumberWithZero;

        if (!SUPPORTED_TIME_UNITS.contains(this.timeUnit)) {
            LOG.warn("Using a currently unsupported/untested timeUnit '" + this.timeUnit + "' for the buckets.");
        }
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getBucketSize() {
        return bucketSize;
    }

    public int getTimeSeriesSize() {
        return timeSeriesSize;
    }

    public boolean isFillNumberWithZero() {
        return fillNumberWithZero;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getBucketContent() {
        return (Class<T>) bucketContent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.timeUnit, this.timeSeriesSize,
                this.bucketSize, this.bucketContent, this.fillNumberWithZero);
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (BucketTimeSeriesConfig.class.equals(obj.getClass())) {
            final BucketTimeSeriesConfig btsc = (BucketTimeSeriesConfig) obj;

            return Objects.equals(this.timeUnit, btsc.timeUnit) &&
                    Objects.equals(this.timeSeriesSize, btsc.timeSeriesSize) &&
                    Objects.equals(this.bucketSize, btsc.bucketSize) &&
                    Objects.equals(this.bucketContent, btsc.bucketContent) &&
                    Objects.equals(this.fillNumberWithZero, btsc.fillNumberWithZero);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("%s-timeSeries[%d]'[%d]'(%s)",
                getTimeUnit().name().toLowerCase(),
                getTimeSeriesSize(),
                getBucketSize(),
                getBucketContent().getSimpleName());
    }
}
