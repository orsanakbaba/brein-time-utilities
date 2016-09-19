package com.brein.time.utils;

import org.junit.Assert;
import org.junit.Test;

public class TestTimeTruncater {

    @Test
    public void testTruncation() {

        // 1473953655L == 09/15/2016 @ 3:34:15pm (UTC)
        // -> 1472688000L == 09/01/2016 @ 0:00:00am (UTC)
        // -> 1473897600L == 09/15/2016 @ 0:00:00am (UTC)
        // -> 1473951600L == 09/15/2016 @ 3:00:00am (UTC)
        // -> 1473953640L == 09/15/2016 @ 3:34:00am (UTC)
        Assert.assertEquals(1472688000L, TimeTruncater.toMonth(1473953655L));
        Assert.assertEquals(1473897600L, TimeTruncater.toDay(1473953655L));
        Assert.assertEquals(1473951600L, TimeTruncater.toHour(1473953655L));
        Assert.assertEquals(1473953640L, TimeTruncater.toMinute(1473953655L));
    }
}
