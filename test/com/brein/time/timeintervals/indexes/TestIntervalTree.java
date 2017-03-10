package com.brein.time.timeintervals.indexes;

import com.brein.time.timeintervals.collections.SetIntervalCollection;
import com.brein.time.timeintervals.intervals.IdInterval;
import com.brein.time.timeintervals.intervals.Interval;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestIntervalTree {
    private static final Logger LOGGER = Logger.getLogger(TestIntervalTree.class);

    @Test
    public void testEmptyTree() {
        assertIsEmpty(new IntervalTree());
    }

    @Test
    public void testTrees() {
        final int nrOfRuns = 100;
        final int minNrOfInserts = 500;
        final int maxNrOfInserts = 1000;

        final Random rnd = new Random();

        for (int i = 0; i < nrOfRuns; i++) {
            final int variableInserts = minNrOfInserts + rnd.nextInt(maxNrOfInserts - minNrOfInserts);
            final int variableRemoves = (int) Math.floor(variableInserts * rnd.nextDouble());

            LOGGER.trace("Validated:" + System.lineSeparator() + createRandomTree(variableInserts, variableRemoves));
        }
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

    @SuppressWarnings("SimplifiableIfStatement")
    public IntervalTree createRandomTree(final int inserts, final int deletes) {
        final IntervalTree tree = new IntervalTree();

        final int totalOperations = inserts + deletes;
        final List<Interval> intervals = new ArrayList<>();
        final Random rnd = new Random();

        // create the intervals that will be inserted
        while (intervals.size() < inserts) {
            final int start = rnd.nextInt(900);
            final Interval interval = new Interval(start, start + rnd.nextInt(100));

            if (!intervals.contains(interval)) {
                intervals.add(interval);
            }
        }

        int totalInserts = 0;
        int totalDeletes = 0;
        final List<Interval> added = new ArrayList<>();

        try {
            for (int i = 0; i < totalOperations; i++) {
                final boolean insert;
                if (added.size() == 0) {
                    insert = true;
                } else if (totalInserts >= inserts) {
                    insert = false;
                } else if (totalDeletes >= deletes) {
                    insert = true;
                } else {
                    insert = rnd.nextInt(2) == 0;
                }

                if (insert) {
                    final Interval interval = intervals.remove(rnd.nextInt(intervals.size()));
                    tree.insert(interval);
                    Assert.assertTrue(added.add(interval));
                    totalInserts++;
                } else {
                    final Interval interval = added.remove(rnd.nextInt(added.size()));
                    Assert.assertTrue(tree.remove(interval));
                    totalDeletes++;
                }

                // validate the tree after each operation
                tree.nodeIterator().forEachRemaining(node -> {
                    if (node.isRoot()) {
                        Assert.assertEquals(0, node.getLevel());
                    }
                    if (node.hasLeft()) {
                        Assert.assertSame(node.getLeft().getParent(), node);
                        Assert.assertEquals(node.getLevel() + 1, node.getLeft().getLevel());
                        Assert.assertTrue(node + " < " + node.getLeft(),
                                (
                                        node.getStart() > node.getLeft().getStart()
                                ) || (
                                        node.getStart() == node.getLeft().getStart() &&
                                                node.getEnd() > node.getLeft().getEnd()
                                )
                        );
                    }
                    if (node.hasRight()) {
                        Assert.assertSame(node.getRight().getParent(), node);
                        Assert.assertEquals(node.getLevel() + 1, node.getRight().getLevel());
                        Assert.assertTrue(node + " > " + node.getRight(),
                                (
                                        node.getStart() < node.getRight().getStart()
                                ) || (
                                        node.getStart() == node.getRight().getStart() &&
                                                node.getEnd() < node.getRight().getEnd()
                                )
                        );
                    }
                });
            }

            LOGGER.info(String.format("inserts: %d (planned: %d), deletes: %d (planned: %d)",
                    totalInserts, inserts, totalDeletes, deletes));
        } catch (final Throwable t) {
            LOGGER.error("Validation failed for: " + System.lineSeparator() + tree.toString(), t);
            throw t;
        }

        return tree;
    }
}
