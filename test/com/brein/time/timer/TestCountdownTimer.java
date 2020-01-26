package com.brein.time.timer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestCountdownTimer {

    private final CountdownTimer timer = new CountdownTimer(1, TimeUnit.SECONDS);

    @Test
    public void testTimer() {

        final long now = System.currentTimeMillis();
        Assert.assertTrue(timer.timerEnded(now, TimeUnit.MILLISECONDS));

        timer.startTimer();
        Assert.assertFalse(timer.timerEnded(now, TimeUnit.MILLISECONDS));
        Assert.assertFalse(timer.timerEnded(now + 5000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(timer.timerEnded(now + 1000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(timer.timerEnded(now + 100000, TimeUnit.MILLISECONDS));

        timer.startTimer();
        Assert.assertFalse(timer.timerEnded(now + 500, TimeUnit.MILLISECONDS));
        Assert.assertTrue(timer.timerEnded(now + 1500, TimeUnit.MILLISECONDS));
    }

    @Test
    public void timerEnded_StartTimer_Not_Called() {

        final long now = System.currentTimeMillis();
        Assert.assertTrue(timer.timerEnded(now, TimeUnit.MILLISECONDS));
    }

    @Test
    public void timerEnded_StartTimer_Called_After_Now(){

        final long now = System.currentTimeMillis();
        timer.startTimer();
        Assert.assertFalse(timer.timerEnded(now, TimeUnit.MILLISECONDS));
    }

    @Test
    public void timerEnded_StartTimer_Called_After_Now_Plus_1000ms(){

        final long now = System.currentTimeMillis();
        timer.startTimer();
        Assert.assertTrue(timer.timerEnded(now + 1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void timerEnded_StartTimer_Called_After_Now_Plus_999ms(){

        final long now = System.currentTimeMillis();
        timer.startTimer();
        Assert.assertFalse(timer.timerEnded(now + 999, TimeUnit.MILLISECONDS));
    }


    @Test
    public void timerEnded_StartTimer_Called_After_Now_Plus_1001ms(){

        final long now = System.currentTimeMillis();
        timer.startTimer();
        Assert.assertTrue(timer.timerEnded(now + 1001, TimeUnit.MILLISECONDS));
    }

    @Test
    public void timerEnded_StartTimer_Called_After_Now_Plus_100000ms(){

        final long now = System.currentTimeMillis();
        timer.startTimer();
        Assert.assertTrue(timer.timerEnded(now + 100000, TimeUnit.MILLISECONDS));
    }
}
