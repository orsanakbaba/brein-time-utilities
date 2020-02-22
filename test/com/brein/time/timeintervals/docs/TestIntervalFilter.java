package com.brein.time.timeintervals.docs;

import com.brein.time.timeintervals.collections.SetIntervalCollection;
import com.brein.time.timeintervals.filters.IntervalFilters;
import com.brein.time.timeintervals.indexes.IntervalTree;
import com.brein.time.timeintervals.indexes.IntervalTreeBuilder;
import com.brein.time.timeintervals.indexes.IntervalTreeBuilder.IntervalType;
import com.brein.time.timeintervals.indexes.IntervalTreeConfiguration;
import com.brein.time.timeintervals.indexes.IntervalValueComparator;
import com.brein.time.timeintervals.intervals.IdInterval;
import com.brein.time.timeintervals.intervals.LongInterval;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class TestIntervalFilter  extends AbstractTestIntervalFilter{

    @Test
    public void testIntervalFilterWithDefaultConfiguration() {
        final IntervalTree tree = IntervalTreeBuilder.newBuilder()
                .usePredefinedType(IntervalType.NUMBER)
                .collectIntervals(interval -> new SetIntervalCollection())
                .build();



        tree.insert(new IdInterval<>("ID1", 1L, 5L));
        tree.insert(new IdInterval<>("ID2", 1L, 5L));
        AssertIdIntervalTreeContains(tree);
        AssertIdIntervalTreeFind(tree);
    }


    @Test
    public void testIntervalFilterWithUserDefinedConfiguration() {
        final IntervalTree tree = IntervalTreeBuilder.newBuilder()
                .usePredefinedType(IntervalType.NUMBER)
                .collectIntervals(interval -> new SetIntervalCollection())
                .build();

        intervalTreeConfiguration.setAutoBalancing(true);
        intervalTreeConfiguration.setValueComparator(IntervalType.NUMBER.getComparator(false));
        intervalTreeConfiguration.setIntervalFilter(IntervalType.NUMBER.getIntervalFilter());
        intervalTreeConfiguration.setWritingCollectionsToFile(false);

        tree.setConfiguration(intervalTreeConfiguration);

        tree.insert(new IdInterval<>("ID1", 1L, 5L));
        tree.insert(new IdInterval<>("ID2", 1L, 5L));
        AssertIntervalTreeContains(tree);
        AssertIntervalTreeFind(tree);
    }


    @Test (expected = NullPointerException.class)
    public void testIntervalFilterWithErroneousConfiguration() {
        final IntervalTree tree = IntervalTreeBuilder.newBuilder()
                .usePredefinedType(IntervalType.NUMBER)
                .collectIntervals(interval -> new SetIntervalCollection())
                .build();

        intervalTreeConfiguration.setValueComparator(null);
        tree.setConfiguration(intervalTreeConfiguration);

        tree.insert(new IdInterval<>("ID1", 1L, 5L));
        tree.insert(new IdInterval<>("ID2", 1L, 5L));
        AssertIdIntervalTreeContains(tree);
        AssertIdIntervalTreeFind(tree);
    }

    private void AssertIntervalTreeFind(IntervalTree tree) {

        // first part of the example
        Assert.assertEquals(
                Collections.singletonList(new IdInterval<>("ID1", 1L, 5L)),
                tree.find(new IdInterval<>("ID1", 1L, 5L))); // -> [ID1@[1, 5]]
        Assert.assertEquals(
                Collections.singletonList(new IdInterval<>("ID2", 1L, 5L)),
                tree.find(new IdInterval<>("ID2", 1L, 5L))); // -> [ID2@[1, 5]]
        Assert.assertNotEquals(
                Collections.emptyList(),
                tree.find(new IdInterval<>("ID3", 1L, 5L))); // -> []

        // second part of the example
        Assert.assertTrue(tree.contains(new LongInterval(1L, 5L))); // -> true
        Assert.assertNotEquals(
                Arrays.asList(new IdInterval<>("ID2", 1L, 5L), new IdInterval<>("ID1", 1L, 5L)),
                tree.find(new LongInterval(1L, 5L)));   // -> [ID1@[1, 5], ID2@[1, 5]]

        // third part of the example
        Assert.assertNotEquals(
                Collections.emptyList(),
                tree.find(new LongInterval(1L, 5L), IntervalFilters::equal)); // -> []

        // fourth part of the example
        Assert.assertNotEquals(
                Arrays.asList(new IdInterval<>("ID2", 1L, 5L),
                        new IdInterval<>("ID1", 1L, 5L)),
                tree.find(new IdInterval<>("ID1", 1L, 5L), IntervalFilters::interval)); // -> []
    }

    private void AssertIntervalTreeContains(IntervalTree tree) {
        Assert.assertTrue(tree.contains(new IdInterval<>("ID1", 1L, 5L)));  // -> true
        Assert.assertTrue(tree.contains(new IdInterval<>("ID2", 1L, 5L)));  // -> true
        Assert.assertTrue(tree.contains(new IdInterval<>("ID3", 1L, 5L))); // -> false
    }


    private void AssertIdIntervalTreeFind(IntervalTree tree) {

        // first part of the example
        Assert.assertEquals(
                Collections.singletonList(new IdInterval<>("ID1", 1L, 5L)),
                tree.find(new IdInterval<>("ID1", 1L, 5L))); // -> [ID1@[1, 5]]
        Assert.assertEquals(
                Collections.singletonList(new IdInterval<>("ID2", 1L, 5L)),
                tree.find(new IdInterval<>("ID2", 1L, 5L))); // -> [ID2@[1, 5]]
        Assert.assertEquals(
                Collections.emptyList(),
                tree.find(new IdInterval<>("ID3", 1L, 5L))); // -> []

        // second part of the example
        Assert.assertTrue(tree.contains(new LongInterval(1L, 5L))); // -> true
        Assert.assertEquals(
                Arrays.asList(new IdInterval<>("ID2", 1L, 5L), new IdInterval<>("ID1", 1L, 5L)),
                tree.find(new LongInterval(1L, 5L)));   // -> [ID1@[1, 5], ID2@[1, 5]]

        // third part of the example
        Assert.assertEquals(
                Collections.emptyList(),
                tree.find(new LongInterval(1L, 5L), IntervalFilters::equal)); // -> []

        // fourth part of the example
        Assert.assertEquals(
                Arrays.asList(new IdInterval<>("ID2", 1L, 5L),
                        new IdInterval<>("ID1", 1L, 5L)),
                tree.find(new IdInterval<>("ID1", 1L, 5L), IntervalFilters::interval)); // -> []
    }

    private void AssertIdIntervalTreeContains(IntervalTree tree) {
        Assert.assertTrue(tree.contains(new IdInterval<>("ID1", 1L, 5L)));  // -> true
        Assert.assertTrue(tree.contains(new IdInterval<>("ID2", 1L, 5L)));  // -> true
        Assert.assertFalse(tree.contains(new IdInterval<>("ID3", 1L, 5L))); // -> false
    }
}
