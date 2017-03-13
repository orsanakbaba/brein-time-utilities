package com.brein.time.timeintervals.intervals;

import org.junit.Assert;
import org.junit.Test;

public class TestInterval {

    @Test
    public void testEqualAndCompare() {
        final IInterval<Long> interval = new Interval<>(1L, 5L);

        //noinspection EqualsWithItself
        Assert.assertTrue(interval.equals(interval));
        Assert.assertEquals(0, interval.compareTo(interval));

        final Interval<Long> interval1 = new Interval<>((Long) null, null);
        Assert.assertEquals(1, interval.compareTo(interval1));
        Assert.assertEquals(-1, interval1.compareTo(interval));

        final Interval<Long> interval2 = new Interval<>(1L, null);
        Assert.assertEquals(-1, interval.compareTo(interval2));
        Assert.assertEquals(1, interval2.compareTo(interval));

        final Interval<Long> interval3 = new Interval<>(1L, 4L);
        Assert.assertEquals(1, interval.compareTo(interval3));
        Assert.assertEquals(-1, interval3.compareTo(interval));

        final Interval<Long> interval4 = new Interval<>(1L, 5L);
        Assert.assertEquals(0, interval.compareTo(interval4));
        Assert.assertEquals(0, interval4.compareTo(interval));
        Assert.assertTrue(interval.equals(interval4));
        Assert.assertTrue(interval4.equals(interval));

        final Interval<Long> interval5 = new Interval<>(Long.class, 1L, 6L, false, true);
        Assert.assertEquals(0, interval.compareTo(interval5));
        Assert.assertEquals(0, interval5.compareTo(interval));
        Assert.assertTrue(interval.equals(interval5));
        Assert.assertTrue(interval5.equals(interval));

        final Interval<Long> interval6 = new Interval<>(1L, 5L);
        Assert.assertEquals(0, interval6.compareTo(new Interval<>(1L, 5L)));
        Assert.assertEquals(0, interval6.compareTo(new Interval<>(1.0, 5.0)));
        Assert.assertEquals(1, interval6.compareTo(new Interval<>(0.9, 1.0)));
        Assert.assertEquals(-1, interval6.compareTo(new Interval<>(1, 6)));

        final Interval<Double> interval7 = new Interval<>(Double.class, 1.0, 5.0, true, true);
        Assert.assertEquals(-1, interval6.compareTo(new Interval<>(Long.class, 1L, 5L, true, true)));
        Assert.assertEquals(1, new Interval<>(Long.class, 1L, 5L, true, true).compareTo(interval6));
    }

    @Test
    public void contains() {
        Assert.assertTrue(new Interval<>(1L, 10L).contains(5));
        Assert.assertTrue(new Interval<>(1L, 10L).contains(1.0));
        Assert.assertFalse(new Interval<>(1L, 10L).contains(10.1));
        Assert.assertTrue(new Interval<>(1L, 10L).contains(10.0));
        Assert.assertTrue(new Interval<>(1L, 10L).contains(10L));
        Assert.assertTrue(new Interval<>(1L, 10L).contains(10));
        Assert.assertTrue(new Interval<>(1L, 1L).contains(1L));
        Assert.assertFalse(new Interval<>(1L, 1L).contains(2L));
        Assert.assertFalse(new Interval<>(1L, 1L).contains(0L));
        Assert.assertFalse(new Interval<>(Long.class, 1L, 2L, false, true).contains(2));
        Assert.assertTrue(new Interval<>(Long.class, 1L, 2L, false, false).contains(2));
    }
}
