package com.brein.time.utils;

import org.junit.Assert;
import org.junit.Test;

public class TestTimeUtils {

    @Test
    public void testFirstOfLastMonth() {
        // 07/01/2016
        final long unixTimestamp = 1467331200;

        // Sometime in august
        final long augustTime = 1470787200;
        Assert.assertEquals(unixTimestamp, TimeUtils.firstOfLastMonthTime(augustTime));
        Assert.assertTrue(TimeUtils.isSameMonth(TimeUtils.firstOfLastMonthTime(augustTime), unixTimestamp));
    }

    @Test
    public void testValidateDate() {
        Assert.assertTrue(TimeUtils.validateDate("11", "05", "2011"));
        Assert.assertTrue(TimeUtils.validateDate("1", "1", "1990"));

        Assert.assertFalse(TimeUtils.validateDate("11", null, null));
        Assert.assertFalse(TimeUtils.validateDate("32", "11", "2010"));
    }
}
