package com.brein.time.timeseries;

import com.brein.time.utils.TimeUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class TestContainerBucketTimeSeries {

    @SuppressWarnings("unchecked")
    @Test
    public void testCombineByContent() {
        final long now = TimeUtils.now();
        final ContainerBucketTimeSeries<HashSet<Integer>, Integer> ts1 =
                new ContainerBucketTimeSeries<>(HashSet::new, new BucketTimeSeriesConfig<>(HashSet.class, TimeUnit.HOURS, 1, 1));
        final ContainerBucketTimeSeries<HashSet<Integer>, Integer> ts2 =
                new ContainerBucketTimeSeries<>(HashSet::new, new BucketTimeSeriesConfig<>(HashSet.class, TimeUnit.HOURS, 1, 1));

        ts1.add(now, 100);
        ts1.add(now, 101);
        ts1.add(now, 102);

        ts2.add(now, 102);
        ts2.add(now, 103);
        ts2.add(now, 104);

        ts1.combineByContent(ts2, (col1, col2) -> {
            col1.addAll(col2);
            return col1;
        });

        Assert.assertEquals(5, ts1.size(0));
        Assert.assertTrue(ts1.get(0).contains(100));
        Assert.assertTrue(ts1.get(0).contains(101));
        Assert.assertTrue(ts1.get(0).contains(102));
        Assert.assertTrue(ts1.get(0).contains(103));
        Assert.assertTrue(ts1.get(0).contains(104));

        // move now a little
        ts2.add(now + 100000, 200);
        ts2.add(now + 100000, 201);
        ts2.add(now + 100000, 202);

        // use the consumer
        ts1.combineByContent(ts2, (BiConsumer<HashSet<Integer>, HashSet<Integer>>) HashSet::addAll);

        Assert.assertEquals(3, ts1.size(0));
        Assert.assertTrue(ts1.get(0).contains(200));
        Assert.assertTrue(ts1.get(0).contains(201));
        Assert.assertTrue(ts1.get(0).contains(202));
    }
}
