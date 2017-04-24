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

    @Test
    public void testDateStringToUnixTimestamp() {
        Assert.assertEquals(1480446690L, TimeUtils.dateStringToUnixTimestamp("2016-11-29 11:11:30",
                "yyyy-MM-dd HH:mm:ss", "America/Los_Angeles"));

        Assert.assertEquals(1480447581L, TimeUtils.dateStringToUnixTimestamp("2016-11-30 4:26:21",
                "yyyy-MM-dd HH:mm:ss", "Asia/Seoul"));

        Assert.assertEquals(1480447581L, TimeUtils.dateStringToUnixTimestamp("2016-11-30 4:26:21",
                "yyyy-MM-dd HH:mm:ss", "Asia/Seoul"));

        Assert.assertEquals(1435019400L, TimeUtils.dateStringToUnixTimestamp("2015-06-22 17:30:00",
                "yyyy-MM-dd HH:mm:ss", "America/Los_Angeles"));
        Assert.assertEquals(1493148300L, TimeUtils.dateStringToUnixTimestamp("2017-04-25 15:25:00",
                "yyyy-MM-dd HH:mm:ss", "America/New_York"));
        Assert.assertEquals(1492348192L, TimeUtils.dateStringToUnixTimestamp("2017-04-16 08:09:52",
                "yyyy-MM-dd HH:mm:ss", "America/Chicago"));
    }

    @Test
    public void testDateStringModification() {
        Assert.assertEquals(1480492799L, TimeUtils.dateStringModifyToUnixTimestamp("2016-11-29 11:11:30",
                "yyyy-MM-dd HH:mm:ss", "America/Los_Angeles", TimeModifier.END_OF_DAY));

        Assert.assertEquals(1480406400L, TimeUtils.dateStringModifyToUnixTimestamp("2016-11-29 11:11:30",
                "yyyy-MM-dd HH:mm:ss", "America/Los_Angeles", TimeModifier.START_OF_DAY));

        Assert.assertEquals(1480485599L, TimeUtils.dateStringModifyToUnixTimestamp("2016-11-29 11:11:30",
                "yyyy-MM-dd HH:mm:ss", "America/Chicago", TimeModifier.END_OF_DAY));

        Assert.assertEquals(1480399200L, TimeUtils.dateStringModifyToUnixTimestamp("2016-11-29 11:11:30",
                "yyyy-MM-dd HH:mm:ss", "America/Chicago", TimeModifier.START_OF_DAY));
    }

    @Test
    public void testTimeUtils() {
        // 07/01/2016
        final long unixTimestamp = 1467331200;

        // Sometime in august
        final long augustTime = 1470787200;
        Assert.assertEquals(unixTimestamp, TimeUtils.firstOfLastMonthTime(augustTime));
        Assert.assertTrue(TimeUtils.isSameMonth(TimeUtils.firstOfLastMonthTime(augustTime), unixTimestamp));
    }

    @Test
    public void testSecondsToFullMinute() {
        Assert.assertEquals(46, TimeUtils.secondsToFullMinute(1473887714L));
        Assert.assertEquals(0, TimeUtils.secondsToFullMinute(1473887700L));

        Assert.assertEquals(16, TimeUtils.secondsToFullMinute(1473887714L, 30));
        Assert.assertEquals(30, TimeUtils.secondsToFullMinute(1473887700L, 30));
        Assert.assertEquals(30, TimeUtils.secondsToFullMinute(1473887700L, 30));
        Assert.assertEquals(0, TimeUtils.secondsToFullMinute(1473887700L, 60));

        int secondCounter = 0;
        for (long i = 0; i < 10000; i++) {
            Assert.assertEquals((60 - secondCounter) % 60, TimeUtils.secondsToFullMinute(i));
            secondCounter = (secondCounter + 1) % 60;
        }
    }

    @Test
    public void testResolveUTC() {
        Assert.assertNotNull(TimeUtils.zoneId(TimeUtils.UTC.getId(), false));
    }

    @Test
    public void testSecondsToPrettyString() {
        Assert.assertEquals("0 seconds", TimeUtils.secondsToPrettyString(0));
        Assert.assertEquals("1 second", TimeUtils.secondsToPrettyString(1));
        Assert.assertEquals("10 seconds", TimeUtils.secondsToPrettyString(10));
        Assert.assertEquals("100 seconds", TimeUtils.secondsToPrettyString(100));
        Assert.assertEquals("17 minutes", TimeUtils.secondsToPrettyString(1000));

        Assert.assertEquals("3 hours", TimeUtils.secondsToPrettyString(10000));
        Assert.assertEquals("28 hours", TimeUtils.secondsToPrettyString(100000));
        Assert.assertEquals("12 days", TimeUtils.secondsToPrettyString(1000000));
        Assert.assertEquals("17 weeks", TimeUtils.secondsToPrettyString(10000000));
        Assert.assertEquals("3 years", TimeUtils.secondsToPrettyString(100000000));

        Assert.assertEquals("0 seconds", TimeUtils.secondsToPrettyString(-0));
        Assert.assertEquals("-1 second", TimeUtils.secondsToPrettyString(-1));
        Assert.assertEquals("-10 seconds", TimeUtils.secondsToPrettyString(-10));
        Assert.assertEquals("-100 seconds", TimeUtils.secondsToPrettyString(-100));
        Assert.assertEquals("-17 minutes", TimeUtils.secondsToPrettyString(-1000));

        Assert.assertEquals("-3 hours", TimeUtils.secondsToPrettyString(-10000));
        Assert.assertEquals("-28 hours", TimeUtils.secondsToPrettyString(-100000));
        Assert.assertEquals("-12 days", TimeUtils.secondsToPrettyString(-1000000));
        Assert.assertEquals("-17 weeks", TimeUtils.secondsToPrettyString(-10000000));
        Assert.assertEquals("-3 years", TimeUtils.secondsToPrettyString(-100000000));

        Assert.assertEquals("119 seconds", TimeUtils.secondsToPrettyString(119));
        Assert.assertEquals("2 minutes", TimeUtils.secondsToPrettyString(120));
        Assert.assertEquals("2 minutes", TimeUtils.secondsToPrettyString(121));
    }
}
