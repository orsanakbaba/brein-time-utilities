package com.brein.time.timeseries;

import com.brein.time.utils.TimeUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Unit tests for the {@code BucketTimeSeries} implementation.
 *
 * @author Philipp Meisen
 */
public class TestBucketTimeSeries {
    private static final Logger LOG = Logger.getLogger(TestBucketTimeSeries.class);

    private final BucketTimeSeries<Set<Integer>> minute_10_1_ts =
            new BucketTimeSeries<>(new BucketTimeSeriesConfig<>(Set.class, TimeUnit.MINUTES, 10, 1));
    private final BucketTimeSeries<Set<Integer>> minute_10_5_ts =
            new BucketTimeSeries<>(new BucketTimeSeriesConfig<>(Set.class, TimeUnit.MINUTES, 10, 5));
    private final BucketTimeSeries<Set<Integer>> minute_10_15_ts =
            new BucketTimeSeries<>(new BucketTimeSeriesConfig<>(Set.class, TimeUnit.MINUTES, 10, 15));
    private final BucketTimeSeries<Set<Integer>> seconds_10_5_ts =
            new BucketTimeSeries<>(new BucketTimeSeriesConfig<>(Set.class, TimeUnit.SECONDS, 10, 5));
    private final BucketTimeSeries<Set<Integer>> seconds_10_1_ts =
            new BucketTimeSeries<>(new BucketTimeSeriesConfig<>(Set.class, TimeUnit.SECONDS, 10, 1));

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

        // set the now the first time
        minute_10_1_ts.setNow(unixTimeStamp);
        Assert.assertEquals(0, minute_10_1_ts.getNowIdx());

        // move one minute forward, which is 1 buckets
        minute_10_1_ts.setNow(unixTimeStamp + 60);
        Assert.assertEquals(9, minute_10_1_ts.getNowIdx());
    }

    @Test
    public void testAdding() {

    }
}
