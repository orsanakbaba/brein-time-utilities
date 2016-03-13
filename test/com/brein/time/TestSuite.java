package com.brein.time;

import com.brein.time.timeseries.TestBucketEndPoints;
import com.brein.time.timeseries.TestBucketTimeSeries;
import com.brein.time.timeseries.gson.TestBucketTimeSeriesTypeConverter;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestBucketTimeSeries.class,
        TestBucketEndPoints.class,
        TestBucketTimeSeriesTypeConverter.class
})
public class TestSuite {
}
