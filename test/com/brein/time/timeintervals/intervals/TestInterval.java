package com.brein.time.timeintervals.intervals;

import org.junit.Assert;
import org.junit.Test;

public class TestInterval {

    @Test
    public void testEqualAndCompare() {
        final Interval interval = new Interval(1L, 5L);

        //noinspection EqualsWithItself
        Assert.assertTrue(interval.equals(interval));
        Assert.assertEquals(0, interval.compareTo(interval));

        final Interval interval1 = new Interval(null, null);
        Assert.assertEquals(1, interval.compareTo(interval1));
        Assert.assertEquals(-1, interval1.compareTo(interval));

        final Interval interval2 = new Interval(1L, null);
        Assert.assertEquals(-1, interval.compareTo(interval2));
        Assert.assertEquals(1, interval2.compareTo(interval));

        final Interval interval3 = new Interval(1L, 4L);
        Assert.assertEquals(1, interval.compareTo(interval3));
        Assert.assertEquals(-1, interval3.compareTo(interval));

        final Interval interval4 = new Interval(1L, 5L);
        Assert.assertEquals(0, interval.compareTo(interval4));
        Assert.assertEquals(0, interval4.compareTo(interval));
        Assert.assertTrue(interval.equals(interval4));
        Assert.assertTrue(interval4.equals(interval));

        final Interval interval5 = new Interval(1L, 6L, false, true);
        Assert.assertEquals(0, interval.compareTo(interval5));
        Assert.assertEquals(0, interval5.compareTo(interval));
        Assert.assertTrue(interval.equals(interval5));
        Assert.assertTrue(interval5.equals(interval));
    }
}
