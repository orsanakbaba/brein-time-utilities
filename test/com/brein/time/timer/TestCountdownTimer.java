package com.brein.time.timer;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestCountdownTimer {

    @Test
    public void testTimer() {
        final CountdownTimer timer = new CountdownTimer(1, TimeUnit.SECONDS);

        final long now = System.currentTimeMillis();
        Assert.assertTrue(timer.timerEnded(now, TimeUnit.MILLISECONDS));

        timer.startTimer();
        Assert.assertFalse(timer.timerEnded(now, TimeUnit.MILLISECONDS));
        Assert.assertFalse(timer.timerEnded(now + 500, TimeUnit.MILLISECONDS));
        Assert.assertTrue(timer.timerEnded(now + 1000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(timer.timerEnded(now + 100000, TimeUnit.MILLISECONDS));

        timer.startTimer();
        Assert.assertFalse(timer.timerEnded(now + 500, TimeUnit.MILLISECONDS));
        Assert.assertTrue(timer.timerEnded(now + 1500, TimeUnit.MILLISECONDS));
    }
}
