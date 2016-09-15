package com.brein.time;

import com.brein.time.utils.TimeUtils;
import org.junit.Assert;
import org.junit.Test;

public class TestTimeUtils {

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
}