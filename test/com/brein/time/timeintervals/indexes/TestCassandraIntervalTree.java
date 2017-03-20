package com.brein.time.timeintervals.indexes;

import com.brein.time.timeintervals.collections.CaffeineIntervalCollectionFactory;
import com.brein.time.timeintervals.collections.CassandraIntervalCollectionPersistor;
import com.brein.time.timeintervals.collections.IntervalCollection;
import com.brein.time.timeintervals.collections.ListIntervalCollection;
import com.brein.time.timeintervals.collections.SetIntervalCollection;
import com.brein.time.timeintervals.intervals.Interval;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    public void testSimpleStorage() {
        final IntervalTree tree = createSampleTree(5L, ListIntervalCollection.class);

        final CaffeineIntervalCollectionFactory factory = createCacheFactory(2L, ListIntervalCollection.class);
        factory.setPersistor(this.persistor);
        
        System.out.println(tree.toString());
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

    protected IntervalTree createSampleTree(
            final long cacheSize,
            final Class<? extends IntervalCollection> type) {

        final CaffeineIntervalCollectionFactory factory = createCacheFactory(cacheSize, type);
        factory.setPersistor(this.persistor);

        final IntervalTree tree = new IntervalTree(factory);

        tree.insert(new Interval(1L, 2L));
        tree.insert(new Interval(5, 10));
        tree.insert(new Interval(5.0, 10.0));
        tree.insert(new Interval(0.1, 1.5));
        tree.insert(new Interval(5, 10));
        tree.insert(new Interval(5.0, 10.0));

        tree.remove(new Interval(5.0, 10.0));

        assertSampleTree(tree, type);

        return tree;
    }

    protected void assertSampleTree(final IntervalTree tree, final Class<? extends IntervalCollection> type) {
        Assert.assertTrue(tree.contains(new Interval(0.1, 1.5)));
        Assert.assertTrue(tree.contains(new Interval(1L, 2L)));
        Assert.assertTrue(tree.contains(new Interval(1, 2)));
        Assert.assertTrue(tree.contains(new Interval(1.0, 2.0)));

        if (type.equals(SetIntervalCollection.class)) {

            // we removed one of the instances, this is the one and only in a set
            Assert.assertFalse(tree.contains(new Interval(5.0, 10.0)));
            Assert.assertFalse(tree.contains(new Interval(5, 10)));
            Assert.assertFalse(tree.contains(new Interval(5L, 10L)));
            Assert.assertEquals(0, tree.find(new Interval(5L, 10L)).size());

        } else if (type.equals(ListIntervalCollection.class)) {

            // we removed one but there are still others in a list
            Assert.assertTrue(tree.contains(new Interval(5.0, 10.0)));
            Assert.assertTrue(tree.contains(new Interval(5, 10)));
            Assert.assertTrue(tree.contains(new Interval(5L, 10L)));
            Assert.assertEquals(3, tree.find(new Interval(5L, 10L)).size());

        } else {
            Assert.fail("Could not validate the sample tree using: " + type);
        }
    }
}
