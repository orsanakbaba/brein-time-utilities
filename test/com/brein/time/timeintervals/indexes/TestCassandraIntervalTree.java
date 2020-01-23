package com.brein.time.timeintervals.indexes;

import com.brein.time.timeintervals.collections.CaffeineIntervalCollectionFactory;
import com.brein.time.timeintervals.collections.CassandraIntervalCollectionPersistor;
import com.brein.time.timeintervals.collections.IntervalCollection;
import com.brein.time.timeintervals.collections.ListIntervalCollection;
import com.brein.time.timeintervals.collections.SetIntervalCollection;
import com.brein.time.timeintervals.filters.IntervalFilters;
import com.brein.time.timeintervals.indexes.IntervalTreeBuilder.IntervalType;
import com.brein.time.timeintervals.intervals.DoubleInterval;
import com.brein.time.timeintervals.intervals.IntegerInterval;
import com.brein.time.timeintervals.intervals.LongInterval;
import com.brein.time.timeintervals.intervals.NumberInterval;
import org.junit.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Ignore
public class TestCassandraIntervalTree {

    private CassandraIntervalCollectionPersistor persistor;

    @Before
    public void setup() {

        // create the persistor, i.e., using Cassandra to store the collections of the tree
        this.persistor = new CassandraIntervalCollectionPersistor();
        this.persistor.setKeySpace("KEYSPACE_" + UUID.randomUUID().toString().replace("-", ""));
        this.persistor.connect("localhost", 9042);
    }

    @Test
    public void testSimpleStorageWithListIntervalCollection() {
        createSampleTree(20, 10L, ListIntervalCollection.class);
    }

    @Test
    public void testSimpleStorageWithSetIntervalCollection() {
        createSampleTree(20, 10L, SetIntervalCollection.class);
    }

    @After
    public void cleanUp() {
        if (this.persistor != null) {
            this.persistor.dropKeySpace();
            this.persistor.close();
        }
    }

    protected CaffeineIntervalCollectionFactory createCacheFactory(final long cacheSize,
                                                                   final Class<? extends IntervalCollection> type) {
        return new CaffeineIntervalCollectionFactory(cacheSize, 1, TimeUnit.DAYS, interval -> {
            try {
                return type.newInstance();
            } catch (final Exception e) {
                Assert.fail("Failed to create a new instance of: " + type);
                return null;
            }
        });
    }

    protected IntervalTree createSampleTree(final int nrOfNodes,
                                            final long cacheSize,
                                            final Class<? extends IntervalCollection> type) {

        final IntervalTree tree = IntervalTreeBuilder.newBuilder()
                .collectIntervals(createCacheFactory(cacheSize, type))
                .usePredefinedType(IntervalType.NUMBER, false)
                .usePersistor(this.persistor)
                .build();

        for (int i = 1; i <= nrOfNodes; i++) {
            for (int k = 1; k <= i; k++) {

                // remove the dummy, on every prev. run
                tree.remove(new NumberInterval<>(Long.class, 1L, 1L));

                // we will have one IntegerInterval with 1 as start, 2 with 2, ...
                tree.insert(new IntegerInterval(i, 1000));

                // we will have one LongInterval with 1 as start, 2 with 2, ...
                tree.insert(new LongInterval((long) i, 1000L));

                // we will have one LongInterval with 1 as start, 2 with 2, ...
                tree.insert(new DoubleInterval((double) i, 1000.0));

                // add also a simple dummy
                tree.add(new NumberInterval<>(Long.class, 1L, 1L));
            }
        }

        assertSampleTree(nrOfNodes, tree, type);

        return tree;
    }

    protected void assertSampleTree(final int nrOfNodes,
                                    final IntervalTree tree,
                                    final Class<? extends IntervalCollection> type) {
        for (int i = 1; i <= nrOfNodes; i++) {

            final DoubleInterval dInterval = new DoubleInterval((double) i, 1000.0);
            final LongInterval lInterval = new LongInterval((long) i, 1000L);
            final IntegerInterval iInterval = new IntegerInterval(i, 1000);

            Assert.assertTrue(dInterval.toString(), tree.contains(dInterval));
            Assert.assertTrue(lInterval.toString(), tree.contains(lInterval));
            Assert.assertTrue(iInterval.toString(), tree.contains(iInterval));
        }

        // dummy is there once
        Assert.assertEquals(1, tree.find(new LongInterval(1L, 1L)).size());
        Assert.assertEquals(1, tree.find(new DoubleInterval(1.0, 1.0)).size());
        Assert.assertEquals(1, tree.find(new IntegerInterval(1, 1)).size());

        if (type.equals(ListIntervalCollection.class)) {

            for (int i = 1; i <= nrOfNodes; i++) {
                Assert.assertEquals(3 * i, tree.find(new LongInterval((long) i, 1000L)).size());
                Assert.assertEquals(3 * i, tree.find(new DoubleInterval((double) i, 1000.0)).size());
                Assert.assertEquals(3 * i, tree.find(new IntegerInterval(i, 1000)).size());

                Assert.assertEquals(i,
                        tree.find(new IntegerInterval(i, 1000), IntervalFilters::strictEqual).size());
                Assert.assertEquals(i,
                        tree.find(new DoubleInterval((double) i, 1000.0), IntervalFilters::strictEqual).size());
                Assert.assertEquals(i,
                        tree.find(new LongInterval((long) i, 1000L), IntervalFilters::strictEqual).size());
            }

        } else if (type.equals(SetIntervalCollection.class)) {

            for (int i = 1; i <= nrOfNodes; i++) {
                Assert.assertEquals(1, tree.find(new LongInterval((long) i, 1000L)).size());
                Assert.assertEquals(1, tree.find(new DoubleInterval((double) i, 1000.0)).size());
                Assert.assertEquals(1, tree.find(new IntegerInterval(i, 1000)).size());
            }
        } else {
            Assert.fail("Could not validate the sample tree using: " + type);
        }
    }
}
