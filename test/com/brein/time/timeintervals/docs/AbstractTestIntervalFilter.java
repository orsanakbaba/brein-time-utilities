package com.brein.time.timeintervals.docs;

import com.brein.time.timeintervals.indexes.IntervalTreeConfiguration;
import org.junit.Before;

public class AbstractTestIntervalFilter {

    protected IntervalTreeConfiguration intervalTreeConfiguration ;
    @Before
    public void prepareDefaultIntervalTreeConfiguration()
    {
        intervalTreeConfiguration = new IntervalTreeConfiguration();
    }
}
