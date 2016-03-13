package com.brein.time.timeseries.gson;

import com.brein.time.timeseries.BucketTimeSeries;
import com.brein.time.timeseries.BucketTimeSeriesConfig;
import com.brein.time.timeseries.ContainerBucketTimeSeries;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TestBucketTimeSeriesTypeConverter {

    static final Gson gson;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(BucketTimeSeries.class, new BucketTimeSeriesTypeConverter());
        gsonBuilder.registerTypeAdapter(ContainerBucketTimeSeries.class, new ContainerBucketTimeSeriesTypeConverter());
        gson = gsonBuilder.create();
    }

    @Test
    public void testBucketTimeSeries() {
        final Random rnd = new Random();
        final BucketTimeSeries<Integer> ts =
                new BucketTimeSeries<>(new BucketTimeSeriesConfig<>(Integer.class, TimeUnit.SECONDS, 10, 1));

        for (int i = 0; i < 1000; i++) {
            ts.set(System.currentTimeMillis() / 1000L - rnd.nextInt(10), rnd.nextInt(1000));
        }

        @SuppressWarnings("unchecked")
        final BucketTimeSeries<Integer> res = gson.fromJson(gson.toJson(ts), BucketTimeSeries.class);
        Assert.assertArrayEquals(ts.order(), res.order());
    }

    @Test
    public void testContainerBucketTimeSeries() {
        BucketTimeSeries res;

        final Random rnd = new Random();
        final ContainerBucketTimeSeries<HashSet<Integer>, Integer> ts =
                new ContainerBucketTimeSeries<>(HashSet::new, new BucketTimeSeriesConfig<>(HashSet.class, TimeUnit.SECONDS, 10, 1));

        res = gson.fromJson(gson.toJson(ts), ContainerBucketTimeSeries.class);
        Assert.assertArrayEquals(ts.order(), res.order());

        for (int i = 0; i < 2; i++) {
            ts.add(System.currentTimeMillis() / 1000L - rnd.nextInt(10), rnd.nextInt(1000));
        }

        res = gson.fromJson(gson.toJson(ts), ContainerBucketTimeSeries.class);
        Assert.assertArrayEquals(ts.order(), res.order());
    }
}
