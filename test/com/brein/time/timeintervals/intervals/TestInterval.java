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
    public void testOverlaps() {
        Assert.assertTrue(new Interval<>(1L, 10L).overlaps(new IdInterval<>("TEST", 5, 100)));
        Assert.assertFalse(new Interval<>(1L, 10L).overlaps(new IdInterval<>("TEST", 11, 100)));
        Assert.assertFalse(new Interval<>(1L, 10L).overlaps(new Interval<>(10.1, 10.2)));
        Assert.assertTrue(new Interval<>(1L, 10L).overlaps(new Interval<>(10L, 10L)));
        Assert.assertTrue(new Interval<>(1L, 10L).overlaps(new Interval<>(-10L, 12L)));

        Assert.assertTrue(new Interval<>(Double.class, 1.0, 5.0, true, true)
                .overlaps(new Interval<>(Double.class, 1.0, 5.0, true, true)));
        Assert.assertFalse(new Interval<>(Double.class, 1.0, 4.9, true, true)
                .overlaps(new Interval<>(Double.class, 4.9, 5.0, true, true)));
        Assert.assertTrue(new Interval<>(Double.class, 1.0, 4.9, true, false)
                .overlaps(new Interval<>(Double.class, 4.9, 5.0, false, true)));
    }

    @Test
    public void testIrOverlaps() {
        Assert.assertTrue(new Interval<>(1L, 10L).irOverlaps(new Interval<>(2L, 11L)));
        Assert.assertFalse(new Interval<>(Long.class.cast(null), null).irOverlaps(new Interval<>(1L, 1L)));
        Assert.assertFalse(new Interval<>(1L, 10L).irOverlaps(new Interval<>(2L, 10L)));
        Assert.assertFalse(new Interval<>(1L, 10L).irOverlaps(new Interval<>(1L, 11L)));

        Assert.assertFalse(new Interval<>(1L, 11L).irIsOverlappedBy(new Interval<>(1L, 10L)));
        Assert.assertTrue(new Interval<>(2L, 11L).irIsOverlappedBy(new Interval<>(1L, 10L)));
        Assert.assertFalse(new Interval<>(Long.class.cast(null), null).irIsOverlappedBy(new Interval<>(1L, 1L)));
    }

    @Test
    public void testIrEquals() {
        Assert.assertTrue(new Interval<>(Long.class.cast(null), null)
                .irEquals(new Interval<>(Long.class.cast(null), null)));
        // see Interval.md
        Assert.assertFalse(new Interval<>(Double.class.cast(null), null)
                .irEquals(new Interval<>(Long.class.cast(null), null)));
        Assert.assertTrue(new Interval<>(5, 7).irEquals(new Interval<>(5L, 7L)));
        Assert.assertTrue(new Interval<>(1.0, 2.0).irEquals(new Interval<>(Long.class, 0L, 2L, true, false)));
        Assert.assertFalse(new Interval<>(1.0, 2.0).irEquals(new Interval<>(Long.class, 0L, 2L, false, false)));
    }

    @Test
    public void testIrBegins() {
        Assert.assertTrue(new Interval<>(Long.class.cast(null), null)
                .irBegins(new Interval<>(Long.class.cast(null), 8L)));
        Assert.assertFalse(new Interval<>(Long.class.cast(null), null)
                .irBegins(new Interval<>(Long.class.cast(null), null)));
        Assert.assertTrue(new Interval<>(5, 7).irBegins(new Interval<>(5L, 6L)));
        Assert.assertFalse(new Interval<>(5, 7).irBegins(new Interval<>(5L, 8L)));
        Assert.assertTrue(new Interval<>(1.0, 2.0).irBegins(new Interval<>(Long.class, 0L, 2L, true, true)));
        Assert.assertFalse(new Interval<>(1.0, 2.0).irBegins(new Interval<>(Long.class, 0L, 1L, false, false)));
        Assert.assertFalse(new Interval<>(Long.class, 0L, 2L, true, true).irBegins(new Interval<>(1.0, 2.0)));
        Assert.assertFalse(new Interval<>(5L, 6L).irBegins(new Interval<>(5, 7)));

        Assert.assertTrue(new Interval<>(Long.class.cast(null), 8L)
                .irBeginsBy(new Interval<>(Long.class.cast(null), null)));
        Assert.assertFalse(new Interval<>(Long.class.cast(null), null)
                .irBeginsBy(new Interval<>(Long.class.cast(null), null)));
        Assert.assertTrue(new Interval<>(Long.class, 0L, 2L, true, true).irBeginsBy(new Interval<>(1.0, 2.0)));
        Assert.assertTrue(new Interval<>(5L, 6L).irBeginsBy(new Interval<>(5, 7)));
        Assert.assertFalse(new Interval<>(1.0, 2.0).irBeginsBy(new Interval<>(Long.class, 0L, 1L, false, false)));
        Assert.assertTrue(new Interval<>(5, 7).irBeginsBy(new Interval<>(5L, 8L)));
    }

    @Test
    public void testIrEnds() {
        // see Interval.md for further information
        Assert.assertFalse(new Interval<>(Double.class.cast(null), null)
                .irEnds(new Interval<>(8L, null)));
        Assert.assertTrue(new Interval<>(Long.class.cast(null), null)
                .irEnds(new Interval<>(8L, null)));
        Assert.assertFalse(new Interval<>(Long.class.cast(null), null)
                .irEnds(new Interval<>(Long.class.cast(null), null)));

        Assert.assertTrue(new Interval<>(8L, null)
                .irEndsBy(new Interval<>(Long.class.cast(null), null)));
    }

    @Test
    public void testStartsDirectlyBefore() {
        Assert.assertTrue(new Interval<>(8L, 9L).irStartsDirectlyBefore(new Interval<>(1L, 7L)));
        Assert.assertTrue(new Interval<>(Double.class, 2.0, 3.0, true, true)
                .irStartsDirectlyBefore(new Interval<>(1.0, 2.0)));
        Assert.assertFalse(new Interval<>(Double.class, 2.0, 3.0, false, true)
                .irStartsDirectlyBefore(new Interval<>(1.0, 2.0)));
    }

    @Test
    public void testEndsDirectlyBefore() {
        Assert.assertTrue(new Interval<>(Long.class.cast(null), 8L).irEndsDirectlyBefore(new Interval<>(9, null)));
        Assert.assertTrue(new Interval<>(Double.class, 1.0, 2.0, true, true)
                .irEndsDirectlyBefore(new Interval<>(2.0, 5.0)));
        Assert.assertFalse(new Interval<>(Double.class, 1.0, 2.0, true, false)
                .irEndsDirectlyBefore(new Interval<>(2.0, 5.0)));
    }

    @Test
    public void testContains() {
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
