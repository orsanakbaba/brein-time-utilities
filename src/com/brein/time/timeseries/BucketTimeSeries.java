package com.brein.time.timeseries;

import com.brein.time.exceptions.IllegalTimePoint;
import com.brein.time.exceptions.IllegalTimePointIndex;
import com.brein.time.exceptions.IllegalTimePointMovement;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
public class BucketTimeSeries<T> {
    protected final BucketTimeSeriesConfig config;

    protected T[] timeSeries = null;
    protected BucketEndPoints now = null;

    protected int currentNowIdx = -1;

    public BucketTimeSeries(final BucketTimeSeriesConfig<T> config) {
        this.config = config;
        this.timeSeries = createEmptyArray();
    }

    @SuppressWarnings("unchecked")
    protected T[] createEmptyArray() {
        final T[] array = (T[]) Array.newInstance(config.getBucketContent(),
                config.getBucketSize());

        if (config.isFillNumberWithZero() && Number.class.isAssignableFrom(config.getBucketContent())) {
            Arrays.fill(array, 0, array.length, 0);
        }

        return array;
    }

    protected void fill(int fromIndex, int endIndex) {
        fromIndex = fromIndex == -1 ? 0 : fromIndex;
        endIndex = endIndex == -1 || endIndex > this.timeSeries.length ? this.timeSeries.length : endIndex;

        if (config.isFillNumberWithZero() && Number.class.isAssignableFrom(config.getBucketContent())) {
            Arrays.fill(this.timeSeries, fromIndex, endIndex, 0);
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
        if (currentNowIdx == -1 || this.now == null) {
            currentNowIdx = 0;

            // keep the new now
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
             * So the calculation is done in three steps:
             * 1.) get the bucket of the new now
             * 2.) determine the difference between the buckets, if it's negative we are done
             * 3.) erase the fields in between and reset to zero or null
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
                    fill(currentNowIdx, newCurrentNowIdx);
                } else {
                    fill(currentNowIdx, -1);
                    fill(0, newCurrentNowIdx);
                }

                // set the values calculated
                this.currentNowIdx = newCurrentNowIdx;
                this.now = newNow;
            }
        }
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

    public void add(final long unixTimeStamp, final T value) {

        // first we have to determine the bucket (idx)
        final BucketEndPoints bucketEndPoints = normalizeUnixTimeStamp(unixTimeStamp);
        final long diff = this.now.diff(bucketEndPoints);

        // we are in the future, let's move there and set it
        if (diff > 0) {
            setNow(unixTimeStamp);
            set(currentNowIdx, value);
        }
        // if we are outside the time, just ignore it
        else if (Math.abs(diff) >= config.getBucketSize()) {
            // do nothing
        }
        // the absolute index has to be made relative (idx(..))
        else {
            set(idx(currentNowIdx + diff), value);
        }
    }

    public void set(final int idx, final T value) throws IllegalTimePointIndex {
        validateIdx(idx);

        this.timeSeries[idx] = value;
    }

    public T get(final int idx) {
        validateIdx(idx);
        return this.timeSeries[idx];
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

    public BucketTimeSeriesConfig getConfig() {
        return config;
    }
}
