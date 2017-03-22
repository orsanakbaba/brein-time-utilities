package com.brein.time.timeintervals.docs;

import com.brein.time.exceptions.IllegalTimeInterval;
import com.brein.time.timeintervals.intervals.DoubleInterval;
import com.brein.time.timeintervals.intervals.IntegerInterval;
import com.brein.time.timeintervals.intervals.LongInterval;
import com.brein.time.timeintervals.intervals.NumberInterval;
import org.junit.Assert;
import org.junit.Test;

public class TestInterval {

    @Test
    public void testDefinitions() {
        final IntegerInterval sample1 = new IntegerInterval(5, 10);   // interval: [5, 10]
        Assert.assertEquals(Integer.valueOf(5), sample1.getNormStart());
        Assert.assertEquals(Integer.valueOf(10), sample1.getNormEnd());

        final LongInterval sample2 = new LongInterval(-123L, 123L);   // interval: [-123, 123]
        Assert.assertEquals(Long.valueOf(-123L), sample2.getNormStart());
        Assert.assertEquals(Long.valueOf(123L), sample2.getNormEnd());

        final DoubleInterval sample3 = new DoubleInterval(1.1, 2.2);  // interval: [1.1, 2.2]
        Assert.assertEquals(1.1, sample3.getNormStart(), 0.0);
        Assert.assertEquals(2.2, sample3.getNormEnd(), 0.0);

        // you can also use the more generic NumberInterval
        final NumberInterval<Integer> i = new NumberInterval<>(Integer.class, 1, 5);  // interval: [1, 5]
        Assert.assertEquals(Integer.valueOf(1), i.getNormStart());
        Assert.assertEquals(Integer.valueOf(5), i.getNormEnd());

        // there are no simplified constructors defined, if you want to use open intervals
        final IntegerInterval openStart = new IntegerInterval(1, 5, true, false);  // interval: (1, 5]
        Assert.assertEquals(Integer.valueOf(1), openStart.getStart());
        Assert.assertEquals(Integer.valueOf(5), openStart.getEnd());
        Assert.assertEquals(Integer.valueOf(2), openStart.getNormStart());
        Assert.assertEquals(Integer.valueOf(5), openStart.getNormEnd());

        final IntegerInterval openEnd = new IntegerInterval(1, 5, false, true);    // interval: [1, 5)
        Assert.assertEquals(Integer.valueOf(1), openEnd.getStart());
        Assert.assertEquals(Integer.valueOf(5), openEnd.getEnd());
        Assert.assertEquals(Integer.valueOf(1), openEnd.getNormStart());
        Assert.assertEquals(Integer.valueOf(4), openEnd.getNormEnd());

        final IntegerInterval bothOpen = new IntegerInterval(1, 5, true, true);    // interval: (1, 5)
        Assert.assertEquals(Integer.valueOf(1), bothOpen.getStart());
        Assert.assertEquals(Integer.valueOf(5), bothOpen.getEnd());
        Assert.assertEquals(Integer.valueOf(2), bothOpen.getNormStart());
        Assert.assertEquals(Integer.valueOf(4), bothOpen.getNormEnd());

        // it is not allowed to specify 'invalid' intervals, i.e., end > start
        try {
            final IntegerInterval invalid1 = new IntegerInterval(5, 4);                // invalid: [5, 4]
        } catch (final IllegalTimeInterval e) {
            Assert.assertTrue(e.getMessage().contains("cannot be smaller"));
        }

        try {
            final IntegerInterval invalid2 = new IntegerInterval(5, 5, true, false);   // invalid: (5, 5]
        } catch (final IllegalTimeInterval e) {
            Assert.assertTrue(e.getMessage().contains("cannot be smaller"));
        }
    }

    @Test
    public void testContains() {
        Assert.assertTrue(new LongInterval(1L, 10L).contains(5));
        Assert.assertTrue(new LongInterval(1L, 10L).contains(1.0));
        Assert.assertFalse(new LongInterval(1L, 10L).contains(10.1));
        Assert.assertTrue(new LongInterval(1L, 10L).contains(10.0));
        Assert.assertFalse(new LongInterval(1L, 2L, false, true).contains(2));
    }

    @Test
    public void testCompareTo() {
        Assert.assertTrue(new LongInterval(1L, 5L).compareTo(new LongInterval(1L, 5L)) == 0);
        Assert.assertTrue(new LongInterval(1L, 5L).compareTo(new DoubleInterval(1.0, 5.0)) == 0);
        Assert.assertTrue(new LongInterval(1L, 5L).compareTo(new DoubleInterval(0.9, 1.0)) > 0);
        Assert.assertTrue(new LongInterval(1L, 5L).compareTo(new IntegerInterval(1, 6)) < 0);

        // the specified type, specifies the boundaries
        Assert.assertTrue(new DoubleInterval(1.0, 5.0, true, true).compareTo(new LongInterval(1L, 5L, true, true)) < 0);
    }

    @Test
    public void testOverlaps() {
        Assert.assertTrue(new LongInterval(1L, 5L).overlaps(new LongInterval(-1L, 10L)));

        Assert.assertFalse(new DoubleInterval(1.0, 4.9, true, true).overlaps(new DoubleInterval(4.9, 5.0, true, true)));
        Assert.assertTrue(new DoubleInterval(1.0, 4.9, true, false)
                .overlaps(new DoubleInterval(4.9, 5.0, false, true)));
    }

    @Test
    public void testEdgeCases() {
        Assert.assertFalse(new DoubleInterval(null, null).irEnds(new LongInterval(8L, null)));
        Assert.assertTrue(new LongInterval(null, null).irEnds(new LongInterval(8L, null)));

        Assert.assertFalse(new DoubleInterval(null, null).irEquals(new LongInterval(null, null)));
    }
}
