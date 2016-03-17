package com.brein.time;

import com.brein.time.timeseries.TestBucketEndPoints;
import com.brein.time.timeseries.TestBucketTimeSeries;
import com.brein.time.timeseries.gson.TestBucketTimeSeriesTypeConverter;
import com.brein.time.timeseries.gson.TestContainerBucketTimeSeriesTypeConverter;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestBucketTimeSeries.class,
        TestBucketEndPoints.class,
        TestBucketTimeSeriesTypeConverter.class,
        TestContainerBucketTimeSeriesTypeConverter.class
})
public class TestSuite {
}
