package com.brein.time.timeseries;

import com.brein.time.exceptions.IllegalBucketEndPoints;
import org.junit.Assert;
import org.junit.Test;

public class TestBucketEndPoints {

    @Test
    public void testSize() {
        Assert.assertEquals(1L, new BucketEndPoints(1L, 2L).size());
        Assert.assertEquals(5L, new BucketEndPoints(1000L, 1005L).size());
    }

    @Test
    public void testDiff() {
        BucketEndPoints b;

        // a bucket with 1 time-point
        b = new BucketEndPoints(1L, 2L);
        Assert.assertEquals(0L, b.diff(new BucketEndPoints(1L, 2L)));
        Assert.assertEquals(1L, b.diff(new BucketEndPoints(2L, 3L)));
        Assert.assertEquals(2L, b.diff(new BucketEndPoints(3L, 4L)));
        Assert.assertEquals(-1L, b.diff(new BucketEndPoints(0L, 1L)));

        // a bucket covering 5 time-points
        b = new BucketEndPoints(1000L, 1005L);
        Assert.assertEquals(1L, b.diff(new BucketEndPoints(1005L, 1010L)));
        Assert.assertEquals(2L, b.diff(new BucketEndPoints(1010L, 1015L)));
    }

    @Test(expected = IllegalBucketEndPoints.class)
    public void testInvalidDiff() {
        BucketEndPoints b;

        // a bucket with 1 time-point
        b = new BucketEndPoints(1L, 2L);
        Assert.assertEquals(0L, b.diff(new BucketEndPoints(1L, 3L)));
    }
}
