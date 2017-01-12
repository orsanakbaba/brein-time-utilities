package com.brein.time.timer;

import java.util.concurrent.TimeUnit;

public class CountdownTimer {

    private final long timeIntervalInNanos;
    private final TimeUnit timeUnit;
    private long intervalStartInNanos = -1;

    /**
     * @param timeInterval Value of time units that the timer should be set to
     * @param timeUnit     Time unit of the time interval
     */
    public CountdownTimer(final long timeInterval,
                          final TimeUnit timeUnit) {
        this.timeIntervalInNanos = timeUnit.toNanos(timeInterval);
        this.timeUnit = timeUnit;
    }

    public void startTimer() {
        startTimer(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    public void startTimer(final long now,
                           final TimeUnit timeUnit) {
        intervalStartInNanos = timeUnit.toNanos(now);
    }

    public boolean timerEnded(final long now,
                              final TimeUnit timeUnit) {
        return intervalStartInNanos == -1 || timeUnit.toNanos(now) - intervalStartInNanos >= timeIntervalInNanos;
    }

    /**
     * @param now      Current unix time
     * @param timeUnit Time unit that current time is given in
     *
     * @return Amount of time left on the timer, in the time unit that the timer was initialized with. If the timer
     * never started to begin with, or if the timer has ended, then return a 0.
     */
    public long getRemainingTime(final long now,
                                 final TimeUnit timeUnit) {
        if (intervalStartInNanos == -1) {
            return 0;
        }

        final long remainingNanos = Math.max(0, timeIntervalInNanos - (timeUnit.toNanos(now) - intervalStartInNanos));
        return TimeUnit.NANOSECONDS.convert(remainingNanos, this.timeUnit);
    }
}
