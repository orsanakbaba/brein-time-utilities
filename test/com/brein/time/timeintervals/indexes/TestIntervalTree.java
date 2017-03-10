package com.brein.time.timeintervals.indexes;

import com.brein.time.timeintervals.collections.SetIntervalCollection;
import com.brein.time.timeintervals.intervals.IdInterval;
import com.brein.time.timeintervals.intervals.Interval;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestIntervalTree {

    @Test
    public void testEmptyTree() {
        assertIsEmpty(new IntervalTree());
    }

    @Test
    public void testEmptyAfterInsertAndDelete() {
        assertIsEmpty(new IntervalTree()
                .insert(new Interval(3L, 3L))
                .insert(new Interval(3L, 3L))
                .insert(new Interval(4L, 4L))
                .insert(new Interval(2L, 2L))
                .delete(new Interval(3L, 3L))
                .delete(new Interval(3L, 3L))
                .delete(new Interval(4L, 4L))
                .delete(new Interval(2L, 2L)));
    }

    @Test
    public void testSetSize() {
        final IntervalTree tree = new IntervalTree(SetIntervalCollection::new);

        Assert.assertEquals(tree.size(), 0);

        tree.insert(new Interval(1, 1));
        tree.insert(new Interval(1, 1));
        tree.insert(new Interval(1, 1));
        Assert.assertEquals(tree.size(), 1);

        tree.remove(new Interval(1, 1));
        Assert.assertEquals(tree.size(), 0);
        tree.remove(new Interval(1, 1));
        Assert.assertEquals(tree.size(), 0);

        tree.insert(new IdInterval<>("ID1", 1, 1));
        tree.insert(new IdInterval<>("ID2", 1, 1));
        Assert.assertEquals(tree.size(), 2);
        tree.remove(new IdInterval<>("ID3", 1, 1));
        Assert.assertEquals(tree.size(), 2);
        tree.remove(new IdInterval<>("ID1", 1, 1));
        Assert.assertEquals(tree.size(), 1);
        tree.remove(new IdInterval<>("ID2", 1, 1));
        Assert.assertEquals(tree.size(), 0);
    }

    @Test
    public void testListSize() {
        final IntervalTree tree = new IntervalTree();

        Assert.assertEquals(tree.size(), 0);

        tree.insert(new Interval(1, 1));
        tree.insert(new Interval(1, 1));
        tree.insert(new Interval(1, 1));
        Assert.assertEquals(tree.size(), 3);

        tree.remove(new Interval(1, 1));
        Assert.assertEquals(tree.size(), 2);
        tree.remove(new Interval(1, 1));
        Assert.assertEquals(tree.size(), 1);

        tree.insert(new IdInterval<>("ID1", 1, 1));
        tree.insert(new IdInterval<>("ID2", 1, 1));
        Assert.assertEquals(tree.size(), 3);

        tree.remove(new IdInterval<>("ID3", 1, 1));
        Assert.assertEquals(tree.size(), 3);
        tree.remove(new IdInterval<>("ID1", 1, 1));
        Assert.assertEquals(tree.size(), 2);
        tree.remove(new IdInterval<>("ID2", 1, 1));
        Assert.assertEquals(tree.size(), 1);
    }

    @Test
    public void testFind() {
        Collection<Interval> found;

        final IntervalTree tree = new IntervalTree(SetIntervalCollection::new);

        tree.insert(new IdInterval<>("ID1", 1L, 5L));
        found = tree.find(new IdInterval<>("ID1", 1L, 5L));
        Assert.assertEquals(1, found.size());

        tree.insert(new IdInterval<>("ID2", 1L, 5L));
        found = tree.find(new IdInterval<>("ID2", 1L, 5L));
        Assert.assertEquals(1, found.size());

        found = tree.find(new Interval(1L, 5L));
        Assert.assertEquals(2, found.size());
    }

    @Test
    public void testOverlap() throws IOException {
        Collection<Interval> overlap;

        final IntervalTree tree = new IntervalTree();

        // check the empty tree
        overlap = tree.overlap(new Interval(1L, 5L));
        Assert.assertEquals(0, overlap.size());

        // add the interval [3, 3]
        tree.insert(new Interval(3L, 3L));

        overlap = tree.overlap(new Interval(1L, 5L));
        Assert.assertEquals(1, overlap.size());
        Assert.assertTrue(overlap.containsAll(Collections.singletonList(
                new Interval(3L, 3L)
        )));

        overlap = tree.overlap(new Interval(4L, 5L));
        Assert.assertEquals(0, overlap.size());

        overlap = tree.overlap(new Interval(1L, 2L));
        Assert.assertEquals(0, overlap.size());

        // add the interval [3, 10]
        tree.insert(new Interval(3L, 10L));

        overlap = tree.overlap(new Interval(1L, 5L));
        Assert.assertEquals(2, overlap.size());
        Assert.assertTrue(overlap.containsAll(Arrays.asList(
                new Interval(3L, 3L),
                new Interval(3L, 10L)
        )));

        // add intervals from [1, x] with x ∈ {1, n}
        final int n = 50;
        tree.clear();
        final List<Integer> shuffledValues = IntStream.range(1, n + 1)
                .boxed().collect(Collectors.toList());
        Collections.shuffle(shuffledValues);
        shuffledValues.forEach(i -> tree.insert(new Interval(1, i)));

        for (int k = 1; k <= n; k++) {
            final Interval query = new Interval(k, k);

            overlap = tree.overlap(query);
            Assert.assertEquals(n - k + 1, overlap.size());
            for (int i = k; i <= n; i++) {
                Assert.assertTrue(overlap.contains(new Interval(1, i)));
            }
        }
    }

    protected boolean assertContains(final IntervalTree tree, final Interval interval) {
        return tree.contains(interval);
    }

    protected void assertIsEmpty(final IntervalTree tree) {
        Assert.assertEquals(0, tree.size());
        Assert.assertTrue(tree.isEmpty());
        Assert.assertFalse(tree.iterator().hasNext());
        Assert.assertFalse(tree.nodeIterator().hasNext());
        Assert.assertFalse(tree.positionIterator().hasNext());
    }
}
