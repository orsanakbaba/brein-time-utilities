package com.brein.time;

import com.brein.time.timeseries.TestBucketEndPoints;
import com.brein.time.timeseries.TestBucketTimeSeries;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestBucketTimeSeries.class,
        TestBucketEndPoints.class
})
public class TestSuite {
}
