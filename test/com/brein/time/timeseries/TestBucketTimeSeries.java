package com.brein.time.timeseries;

import com.brein.time.exceptions.IllegalConfiguration;
import com.brein.time.utils.TimeUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Unit tests for the {@code BucketTimeSeries} implementation.
 *
 * @author Philipp Meisen
 */
public class TestBucketTimeSeries {
    private static final Logger LOG = Logger.getLogger(TestBucketTimeSeries.class);

    private final BucketTimeSeries<Integer> minute_10_1_ts =
            new BucketTimeSeries<>(new BucketTimeSeriesConfig<>(Integer.class, TimeUnit.MINUTES, 10, 1));
    private final BucketTimeSeries<Integer> minute_10_5_ts =
            new BucketTimeSeries<>(new BucketTimeSeriesConfig<>(Integer.class, TimeUnit.MINUTES, 10, 5));
    private final BucketTimeSeries<Integer> minute_10_15_ts =
            new BucketTimeSeries<>(new BucketTimeSeriesConfig<>(Integer.class, TimeUnit.MINUTES, 10, 15));
    private final BucketTimeSeries<Integer> seconds_10_5_ts =
            new BucketTimeSeries<>(new BucketTimeSeriesConfig<>(Integer.class, TimeUnit.SECONDS, 10, 5));
    private final BucketTimeSeries<Integer> seconds_10_1_ts =
            new BucketTimeSeries<>(new BucketTimeSeriesConfig<>(Integer.class, TimeUnit.SECONDS, 10, 1));

    private final BiConsumer<Integer[], Integer[]> zeroValidator = (z, ts) -> {
        final Set<Integer> zeros = new HashSet<>(Arrays.asList(z));
        for (int i = 0; i < 10; i++) {
            if (zeros.contains(i)) {
                Assert.assertTrue(" 0 expected at: " + i, ts[i] == 0);
            } else {
                Assert.assertTrue("!0 expected at: " + i, ts[i] != 0);
            }
        }
    };

    @Test
    public void testNormalizeUnixTimeStamp() {

        BucketEndPoints res;
        long unixTimeStamp;

        /*
         * 1456980064 -> 03/03/2016 @ 4:41am (UTC)
         */
        unixTimeStamp = 1456980064;
        LOG.info(String.format("Testing %d (%s):", unixTimeStamp, TimeUtils.format(unixTimeStamp)));

        // [2016-03-03 04:40:00 UTC, 2016-03-03 04:45:00 UTC)
        res = minute_10_5_ts.normalizeUnixTimeStamp(unixTimeStamp);
        LOG.info(String.format("Result with %s: %s", minute_10_5_ts.getConfig(), res));
        Assert.assertEquals(1456980000, res.getUnixTimeStampStart());
        Assert.assertEquals(1456980300, res.getUnixTimeStampEnd());

        // [2016-03-03 04:30:00 UTC, 2016-03-03 04:45:00 UTC)
        res = minute_10_15_ts.normalizeUnixTimeStamp(unixTimeStamp);
        LOG.info(String.format("Result with %s: %s", minute_10_15_ts.getConfig(), res));
        Assert.assertEquals(1456979400, res.getUnixTimeStampStart());
        Assert.assertEquals(1456980300, res.getUnixTimeStampEnd());

        // [2016-03-03 04:41:00 UTC, 2016-03-03 04:41:05 UTC)
        res = seconds_10_5_ts.normalizeUnixTimeStamp(unixTimeStamp);
        LOG.info(String.format("Result with %s: %s", seconds_10_5_ts.getConfig(), res));
        Assert.assertEquals(1456980060, res.getUnixTimeStampStart());
        Assert.assertEquals(1456980065, res.getUnixTimeStampEnd());

        // [2016-03-03 04:41:00 UTC, 2016-03-03 04:42:00 UTC)
        res = minute_10_1_ts.normalizeUnixTimeStamp(unixTimeStamp);
        LOG.info(String.format("Result with %s: %s", minute_10_1_ts.getConfig(), res));
        Assert.assertEquals(1456980060, res.getUnixTimeStampStart());
        Assert.assertEquals(1456980120, res.getUnixTimeStampEnd());

        // [2016-03-03 04:41:00 UTC, 2016-03-03 04:42:00 UTC)
        res = seconds_10_1_ts.normalizeUnixTimeStamp(unixTimeStamp);
        LOG.info(String.format("Result with %s: %s", seconds_10_1_ts.getConfig(), res));
        Assert.assertEquals(1456980064, res.getUnixTimeStampStart());
        Assert.assertEquals(1456980065, res.getUnixTimeStampEnd());
    }


    @Test
    public void testMoveNow() {

        /*
         * 1456980064 -> 03/03/2016 @ 4:41am (UTC)
         */
        long unixTimeStamp = 1456980064;
        minute_10_1_ts.timeSeries = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        // set the now the first time
        minute_10_1_ts.setNow(unixTimeStamp);
        Assert.assertEquals(0, minute_10_1_ts.getNowIdx());
        zeroValidator.accept(new Integer[]{}, minute_10_1_ts.timeSeries);

        // move one minute forward, which is 1 buckets
        minute_10_1_ts.setNow(unixTimeStamp + 60);
        Assert.assertEquals(9, minute_10_1_ts.getNowIdx());
        zeroValidator.accept(new Integer[]{9}, minute_10_1_ts.timeSeries);

        minute_10_1_ts.setNow(unixTimeStamp + 120);
        Assert.assertEquals(8, minute_10_1_ts.getNowIdx());
        zeroValidator.accept(new Integer[]{8, 9}, minute_10_1_ts.timeSeries);

        minute_10_1_ts.setNow(unixTimeStamp + 660);
        Assert.assertEquals(9, minute_10_1_ts.getNowIdx());
        zeroValidator.accept(new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, minute_10_1_ts.timeSeries);

    }

    @Test
    public void testOrder() {

        /*
         * 1456980064 -> 03/03/2016 @ 4:41am (UTC)
         */
        long unixTimeStamp = 1456980064;

        for (int i = 0; i < 10; i++) {
            minute_10_1_ts.set(unixTimeStamp - i * 60, i);
        }

        final List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            minute_10_1_ts.setNow(unixTimeStamp + i * 60);
            expected.add(i);

            zeroValidator.accept(expected.toArray(new Integer[expected.size()]),
                    minute_10_1_ts.order());
        }
    }

    @Test
    public void testTypes() {
        long[] res;

        final BucketTimeSeries<Integer> subject1 = new BucketTimeSeries<>(
                new BucketTimeSeriesConfig<>(Integer.class,
                        TimeUnit.MINUTES, 5, 2));
        res = subject1.create(Integer::longValue);
        Assert.assertArrayEquals(new long[]{0L, 0L, 0L, 0L, 0L}, res);

        subject1.set(1L, 100);
        res = subject1.create(Integer::longValue);
        Assert.assertArrayEquals(new long[]{100L, 0L, 0L, 0L, 0L}, res);

        final BucketTimeSeries<Double> subject2 = new BucketTimeSeries<>(
                new BucketTimeSeriesConfig<>(Double.class,
                        TimeUnit.MINUTES, 5, 10));
        res = subject2.create(Double::longValue);
        Assert.assertArrayEquals(new long[]{0L, 0L, 0L, 0L, 0L}, res);

        subject2.set(1L, 250.61);
        res = subject2.create(Double::longValue);
        Assert.assertArrayEquals(new long[]{250L, 0L, 0L, 0L, 0L}, res);

        subject2.set(1L + 60 * 10L, 350.61);
        res = subject2.create(Double::longValue);
        Assert.assertArrayEquals(new long[]{350L, 250L, 0L, 0L, 0L}, res);
    }

    @Test(expected = IllegalConfiguration.class)
    public void testCombineFailure1() {
        minute_10_1_ts.combine(minute_10_5_ts);
    }

    @Test(expected = IllegalConfiguration.class)
    public void testCombineFailure2() {
        minute_10_1_ts.combine(seconds_10_1_ts);
    }

    @Test
    public void testCombine() {
        seconds_10_1_ts.combine(seconds_10_1_ts);
        Assert.assertArrayEquals(new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, seconds_10_1_ts.order());

        seconds_10_1_ts.combine(new BucketTimeSeries<>(seconds_10_1_ts.getConfig(), new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 1L));
        Assert.assertEquals(1L, seconds_10_1_ts.getNow());
        Assert.assertArrayEquals(new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, seconds_10_1_ts.order());

        seconds_10_1_ts.combine(new BucketTimeSeries<>(seconds_10_1_ts.getConfig(), new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 2L));
        Assert.assertEquals(2L, seconds_10_1_ts.getNow());
        Assert.assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, seconds_10_1_ts.order());

        seconds_10_1_ts.combine(new BucketTimeSeries<>(seconds_10_1_ts.getConfig(), new Integer[]{-2, -3, -4, -5, -6, -7, -8, -9, -10, -100}, 1L));
        Assert.assertEquals(2L, seconds_10_1_ts.getNow());
        Assert.assertArrayEquals(new Integer[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, seconds_10_1_ts.order());

        seconds_10_1_ts.combine(new BucketTimeSeries<>(seconds_10_1_ts.getConfig(), new Integer[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5}, 100L));
        Assert.assertEquals(100L, seconds_10_1_ts.getNow());
        Assert.assertArrayEquals(new Integer[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5}, seconds_10_1_ts.order());

        seconds_10_1_ts.combine(new BucketTimeSeries<>(seconds_10_1_ts.getConfig(), new Integer[]{6, 6, 6, 6, 6, 6, 6, 6, 6, 6}, 10L));
        Assert.assertEquals(100L, seconds_10_1_ts.getNow());
        Assert.assertArrayEquals(new Integer[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5}, seconds_10_1_ts.order());
    }

    @Test
    public void testSum() {
        final BucketTimeSeries<Double> subject = new BucketTimeSeries<>(
                new BucketTimeSeriesConfig<>(Long.class,
                        TimeUnit.MINUTES, 5, 10));

        Assert.assertEquals(0, subject.sumTimeSeries());
    }
}
