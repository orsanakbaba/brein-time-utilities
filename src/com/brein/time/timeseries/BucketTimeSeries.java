package com.brein.time.timeseries;

import com.brein.time.exceptions.*;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This implementation represents a time-series. Each time-point of the series
 * represents several actual time-points on the underlying time-axis (buckets).
 * <p>
 * <pre>
 * The data structure (which is based on an array) can be explained best with
 * an illustration (with n == timeSeriesSize):
 *
 *   [0] [1] [2] [3] [4] [5] [6] ... [n]
 *        ↑
 *  currentNowIdx
 *
 * Each array field is a bucket of time-stamps (ordered back from now):
 *
 *   [1] ==> [1456980000, 1456980300) ← now, 1 == currentNowIdx
 *   [2] ==> [1456970700, 1456980000)
 *   ...
 *   [n] ==> ...
 *   [0] ==> ...
 * </pre>
 *
 * @param <T> the content held by the time-series
 * @author Philipp
 */
public class BucketTimeSeries<T extends Serializable> implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 1L;

    protected final BucketTimeSeriesConfig<T> config;

    protected T[] timeSeries = null;
    protected BucketEndPoints now = null;

    protected int currentNowIdx = -1;

    public BucketTimeSeries(final BucketTimeSeriesConfig<T> config) {
        this.config = config;
        this.timeSeries = createEmptyArray();
    }

    /**
     * Constructor to create a pre-set time series.
     *
     * @param config     the configuration to use
     * @param timeSeries the initiale time series
     * @param now        the current now timestamp
     */
    public BucketTimeSeries(final BucketTimeSeriesConfig<T> config, final T[] timeSeries, final long now) throws IllegalValueRegardingConfiguration {
        this.config = config;
        this.timeSeries = timeSeries;

        this.now = normalizeUnixTimeStamp(now);
        this.currentNowIdx = 0;

        if (this.timeSeries != null && this.timeSeries.length != config.getTimeSeriesSize()) {
            throw new IllegalValueRegardingConfiguration("The defined size of the time-series does not satisfy the configured time-series size (" + this.timeSeries.length + " vs. " + config.getTimeSeriesSize() + ").");
        }
    }

    @SuppressWarnings("unchecked")
    protected T[] createEmptyArray() {
        final T[] array = (T[]) Array.newInstance(config.getBucketContent(),
                config.getTimeSeriesSize());

        if (applyZero()) {
            Arrays.fill(array, 0, array.length, zero());
        }

        return array;
    }

    protected boolean applyZero() {
        return config.isFillNumberWithZero() && Number.class.isAssignableFrom(config.getBucketContent());
    }

    /**
     * Resets the values from [fromIndex, endIndex).
     *
     * @param fromIndex the index to start from (included)
     * @param endIndex  the index to end (excluded)
     */
    protected void fill(int fromIndex, int endIndex) {
        fromIndex = fromIndex == -1 ? 0 : fromIndex;
        endIndex = endIndex == -1 || endIndex > this.timeSeries.length ? this.timeSeries.length : endIndex;

        if (applyZero()) {
            Arrays.fill(this.timeSeries, fromIndex, endIndex, zero());
        } else {
            Arrays.fill(this.timeSeries, fromIndex, endIndex, null);
        }
    }

    /**
     * Modifies the "now" unix time stamp of the time-series. This modifies,
     * the time-series, i.e., data might be removed if the data is pushed.
     *
     * @param unixTimeStamp the new now to be used
     * @throws IllegalTimePointMovement if the new unix time stamp it moved
     *                                  into the past, e.g., if the current
     *                                  time stamp is newers
     */
    public void setNow(final long unixTimeStamp) throws IllegalTimePointMovement {

        /*
         * "now" strongly depends on the TimeUnit used for the timeSeries, as
         * well as the bucketSize. If, e.g., the TimeUnit is MINUTES and the
         * bucketSize is 5, a unix time stamp representing 01/20/1981 08:07:30
         * must be mapped to 01/20/1981 08:10:00 (the next valid bucket).
         */
        if (this.currentNowIdx == -1 || this.now == null) {
            this.currentNowIdx = 0;
            this.now = normalizeUnixTimeStamp(unixTimeStamp);
        } else {

            /*
             * Getting the new currentNowIdx is done by calculating the
             * difference between the old now and the new now and moving
             * the currentNowIdx forward.
             *
             *  [0] [1] [2] [3] [4] [5] [6]
             *       ↑
             * currentNowIdx
             *
             * Assume we move the now time stamp forward by three buckets:
             *
             *  [0] [1] [2] [3] [4] [5] [6]
             *                       ↑
             *                 currentNowIdx
             *
             * So the calculation is done in two steps:
             * 1.) get the bucket of the new now
             * 2.) determine the difference between the buckets, if it's negative => error,
             *     if it is zero => done, otherwise => erase the fields in between and reset
             *     to zero or null
             */
            final BucketEndPoints newNow = normalizeUnixTimeStamp(unixTimeStamp);
            final long diff = this.now.diff(newNow);

            if (diff < 0) {
                throw new IllegalTimePointMovement("Cannot move to the past");
            } else if (diff > 0) {
                final int newCurrentNowIdx = idx(currentNowIdx - diff);

                /*
                 * Remove the "passed" information. There are several things we have to
                 * consider:
                 *  1.) the whole array has to be reset
                 *  2.) the array has to be reset partly forward
                 *  3.) the array has to be reset "around the corner"
                 */
                if (diff >= config.getTimeSeriesSize()) {
                    fill(-1, -1);
                } else if (newCurrentNowIdx > currentNowIdx) {
                    fill(0, currentNowIdx);
                    fill(newCurrentNowIdx, -1);
                } else {
                    fill(newCurrentNowIdx, currentNowIdx);
                }

                // set the values calculated
                this.currentNowIdx = newCurrentNowIdx;
                this.now = newNow;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public T[] order() {
        final T[] result;

        if (this.timeSeries != null && this.currentNowIdx != -1) {
            result = (T[]) Array.newInstance(config.getBucketContent(),
                    config.getTimeSeriesSize());

            final AtomicInteger i = new AtomicInteger(0);
            forEach(val -> result[i.getAndIncrement()] = val);
        } else {
            result = createEmptyArray();
        }

        return result;
    }

    public long[] create(final Function<T, Long> supplier) {
        final long[] result = new long[config.getTimeSeriesSize()];

        if (this.timeSeries != null && this.currentNowIdx != -1) {
            final AtomicInteger i = new AtomicInteger(0);
            forEach(val -> result[i.getAndIncrement()] = supplier.apply(val));
        } else {
            final boolean applyZero = applyZero();

            // just assume we have nulls everywhere
            for (int i = 0; i < result.length; i++) {
                if (applyZero) {
                    result[i] = supplier.apply(zero());
                } else {
                    result[i] = supplier.apply(null);
                }
            }
        }

        return result;
    }

    public int getNowIdx() {
        return currentNowIdx;
    }

    public BucketEndPoints getEndPoints(final int idx) throws IllegalTimePoint {

        if (currentNowIdx == -1 || now == null) {
            throw new IllegalTimePoint("The now is not set yet, thus no end-points can be returned");
        }

        return now.move(idx);
    }

    public void set(final long unixTimeStamp, final T value) {
        final int idx = handleDataUnixTimeStamp(unixTimeStamp);
        if (idx == -1) {
            return;
        }

        set(idx, value);
    }

    public void modify(final long unixTimeStamp, final Function<T, T> mod) {
        final int idx = handleDataUnixTimeStamp(unixTimeStamp);
        if (idx == -1) {
            return;
        }

        modify(idx, mod);
    }

    public void modify(final int idx, final Function<T, T> mod) {
        set(idx, mod.apply(get(idx)));
    }

    public void set(final int idx, final T value) throws IllegalTimePointIndex {
        validateIdx(idx);

        this.timeSeries[idx] = value;
    }

    public T get(final int idx) {
        validateIdx(idx);
        return this.timeSeries[idx];
    }

    protected int handleDataUnixTimeStamp(final long unixTimeStamp) {

        // first we have to determine the bucket (idx)
        final BucketEndPoints bucketEndPoints = normalizeUnixTimeStamp(unixTimeStamp);

        final long diff;
        if (this.now == null) {
            setNow(unixTimeStamp);
            diff = 0L;
        } else {
            diff = this.now.diff(bucketEndPoints);
        }

        // we are in the future, let's move there and set it
        if (diff > 0) {
            setNow(unixTimeStamp);
            return currentNowIdx;
        }
        // if we are outside the time, just ignore it
        else if (Math.abs(diff) >= config.getTimeSeriesSize()) {
            // do nothing
            return -1;
        }
        // the absolute index has to be made relative (idx(..))
        else {
            return idx(currentNowIdx - diff);
        }
    }

    protected void validateIdx(final int idx) throws IllegalTimePointIndex {
        if (idx < 0 || idx >= config.getTimeSeriesSize()) {
            throw new IllegalTimePointIndex(String.format("The index %d is out of bound [%d, %d].", idx, 0, config.getTimeSeriesSize() - 1));
        }
    }

    protected int idx(final long absIdx) {
        /*
         * The absolute index has to be mapped to a real array index. This is done
         * by using the modulo operation. Nevertheless, the module has a range of
         * -1 * config.getTimeSeriesSize() to config.getTimeSeriesSize(). Thus,
         * if negative we have to add the config.getTimeSeriesSize() once.
         */
        final int idx = (int) (absIdx % config.getTimeSeriesSize());
        return idx < 0 ? idx + config.getTimeSeriesSize() : idx;
    }

    protected BucketEndPoints normalizeUnixTimeStamp(final long unixTimeStamp) {
        final TimeUnit timeUnit = config.getTimeUnit();

        // first get the time stamp in the unit of the time-series
        final long timeStamp = timeUnit.convert(unixTimeStamp, TimeUnit.SECONDS);

        /*
         * Now lets, normalize the time stamp regarding to the bucketSize:
         *  1.) we need the size of a bucket
         *  2.) we need where the current time stamp is located within the size,
         *      i.e., how much it reaches into the bucket (ratio => offset)
         *  3.) we use the calculated offset to determine the end points (in seconds)
         *
         *  Example:
         *    The time stamp 1002 (in minutes or seconds or ...?) should be mapped
         *    to a normalized bucket, so the bucket would be, e.g., [1000, 1005).
         */
        final int bucketSize = config.getBucketSize();
        final double ratio = timeStamp / bucketSize;
        final long offset = timeStamp % bucketSize;
        final long start = timeStamp - offset;
        final long end = start + config.getBucketSize();

        return new BucketEndPoints(TimeUnit.SECONDS.convert(start, timeUnit),
                TimeUnit.SECONDS.convert(end, timeUnit));
    }

    @SuppressWarnings("unchecked")
    protected T zero() {
        final Class<?> contentType = config.getBucketContent();

        if (Number.class.isAssignableFrom(contentType)) {

            if (Byte.class.equals(contentType)) {
                return (T) Byte.valueOf((byte) 0);
            } else if (Short.class.equals(contentType)) {
                return (T) Short.valueOf((short) 0);
            } else if (Integer.class.equals(contentType)) {
                return (T) Integer.valueOf(0);
            } else if (Long.class.equals(contentType)) {
                return (T) Long.valueOf(0L);
            } else if (Double.class.equals(contentType)) {
                return (T) Double.valueOf(0.0);
            } else if (Float.class.equals(contentType)) {
                return (T) Float.valueOf(0.0f);
            } else if (BigDecimal.class.equals(contentType)) {
                return (T) BigDecimal.valueOf(0);
            } else if (BigInteger.class.equals(contentType)) {
                return (T) BigInteger.valueOf(0);
            } else if (AtomicInteger.class.equals(contentType)) {
                return (T) new AtomicInteger(0);
            } else if (AtomicLong.class.equals(contentType)) {
                return (T) new AtomicLong(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public BucketTimeSeriesConfig<T> getConfig() {
        return config;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            final int currentIdx = getNowIdx();
            final int size = BucketTimeSeries.this.config.getTimeSeriesSize();
            int offset = 0;

            @Override
            public boolean hasNext() {
                return offset < size;
            }

            @Override
            public T next() {
                final int idx = BucketTimeSeries.this.idx(currentIdx + offset);
                offset++;

                return BucketTimeSeries.this.timeSeries[idx];
            }
        };
    }

    public long getNow() {
        if (now == null) {
            return -1L;
        } else {
            return now.getUnixTimeStampEnd() - 1;
        }
    }

    public void setTimeSeries(final T[] timeSeries, final long now) {
        this.now = normalizeUnixTimeStamp(now);
        this.currentNowIdx = 0;
        this.timeSeries = timeSeries;
    }

    public void combine(final BucketTimeSeries<T> timeSeries) throws IllegalConfiguration {
        combine(timeSeries, this::addition);
    }

    public void combine(final BucketTimeSeries<T> timeSeries, final BiFunction<T, T, T> cmb) throws IllegalConfiguration {
        if (!Objects.equals(timeSeries.config, config)) {
            throw new IllegalConfiguration("The time-series to combine must have the same configuration.");
        }

        final int cmp = Long.compare(getNow(), timeSeries.getNow());
        final BucketTimeSeries<T> ts;

        if (cmp != 0 && getNow() == -1) {
            ts = timeSeries;
            setNow(timeSeries.getNow());
        } else if (cmp != 0 && timeSeries.getNow() == -1) {
            ts = timeSeries;
        } else {
            if (cmp == 0) {
                ts = timeSeries;
            } else if (cmp > 0) {

                // the passed time-series is in the past
                ts = new BucketTimeSeries<>(timeSeries.getConfig(), timeSeries.timeSeries, timeSeries.getNow());
                ts.setNow(this.getNow());
            } else {

                // the passed time-series is in the future
                this.setNow(timeSeries.getNow());
                ts = timeSeries;
            }
        }

        for (int i = 0; i < config.getTimeSeriesSize(); i++) {
            final int idx = idx(currentNowIdx + i);
            set(idx, cmb.apply(get(idx), ts.get(ts.idx(ts.currentNowIdx + i))));
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(order());
    }

    @SuppressWarnings("unchecked")
    public T addition(final T a, final T b) {
        if (a == null && b == null) {
            return null;
        } else if (a == null) {
            return addition(zero(), b);
        } else if (b == null) {
            return addition(a, zero());
        }

        final Class<T> contentType = (Class<T>) a.getClass();
        if (Number.class.isAssignableFrom(contentType)) {

            if (Byte.class.equals(contentType)) {
                return (T) Byte.valueOf(Integer.valueOf(Byte.class.cast(a) + Byte.class.cast(b)).byteValue());
            } else if (Short.class.equals(contentType)) {
                return (T) Short.valueOf(Integer.valueOf(Short.class.cast(a) + Short.class.cast(b)).shortValue());
            } else if (Integer.class.equals(contentType)) {
                return (T) Integer.valueOf(Integer.class.cast(a) + Integer.class.cast(b));
            } else if (Long.class.equals(contentType)) {
                return (T) Long.valueOf(Long.class.cast(a) + Long.class.cast(b));
            } else if (Double.class.equals(contentType)) {
                return (T) Double.valueOf(Double.class.cast(a) + Double.class.cast(b));
            } else if (Float.class.equals(contentType)) {
                return (T) Float.valueOf(Float.class.cast(a) + Float.class.cast(b));
            } else if (BigDecimal.class.equals(contentType)) {
                return (T) BigDecimal.class.cast(a).add(BigDecimal.class.cast(b));
            } else if (BigInteger.class.equals(contentType)) {
                return (T) BigInteger.class.cast(a).add(BigInteger.class.cast(b));
            } else if (AtomicInteger.class.equals(contentType)) {
                final int intA = AtomicInteger.class.cast(a).intValue();
                final int intB = AtomicInteger.class.cast(b).intValue();
                return (T) new AtomicInteger(intA + intB);
            } else if (AtomicLong.class.equals(contentType)) {
                final long longA = AtomicLong.class.cast(a).longValue();
                final long longB = AtomicLong.class.cast(b).longValue();
                return (T) new AtomicLong(longA + longB);
            } else {
                return null;
            }
        } else if (String.class.isAssignableFrom(contentType)) {
            return (T) (String.class.cast(a) + String.class.cast(b));
        } else if (List.class.isAssignableFrom(contentType)) {
            final List<T> list = new ArrayList<>();
            list.addAll(List.class.cast(a));
            list.addAll(List.class.cast(b));
            return (T) list;
        } else if (Set.class.isAssignableFrom(contentType)) {
            final Set<T> set = new HashSet<>();
            set.addAll(Set.class.cast(a));
            set.addAll(Set.class.cast(b));
            return (T) set;
        } else {
            return null;
        }
    }
}
