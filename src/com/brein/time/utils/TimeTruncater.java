package com.brein.time.utils;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TimeTruncater {
    private static final ZoneId UTC = ZoneOffset.UTC;

    /**
     * Truncates the passed time-stamp to the first of the month information only, i.e., the 09/10/2016 00:14:43 will be
     * truncated to 01/10/2016 00:00:00.
     *
     * @param unixTimeStamp the time-stamp to be truncated
     *
     * @return the date information only
     */
    public static long toMonth(final long unixTimeStamp) {
        final Instant instant = Instant.ofEpochSecond(unixTimeStamp);
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, UTC);
        return zonedDateTime.withDayOfMonth(1)
                .with(LocalTime.of(0, 0))
                .toEpochSecond();
    }

    /**
     * Truncates the passed time-stamp to the date information only, i.e., the 09/10/2016 00:14:43 will be truncated to
     * 09/10/2016 00:00:00.
     *
     * @param unixTimeStamp the time-stamp to be truncated
     *
     * @return the date information only
     */
    public static long toDay(final long unixTimeStamp) {
        final Instant instant = Instant.ofEpochSecond(unixTimeStamp);
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, UTC);
        return zonedDateTime.with(LocalTime.of(0, 0, 0)).toEpochSecond();
    }

    public static long toEndOfDay(final long unixTimeStamp) {
        final Instant instant = Instant.ofEpochSecond(unixTimeStamp);
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, UTC);
        return zonedDateTime.with(LocalTime.of(23, 59, 59)).toEpochSecond();
    }

    /**
     * Truncates the minute and second from the passed time-stamp, i.e., the 09/10/2016 02:14:43 will be truncated to
     * 09/10/2016 02:00:00.
     *
     * @param unixTimeStamp the time-stamp to be truncated
     *
     * @return the truncated time-stamp
     */
    public static long toHour(final long unixTimeStamp) {
        final Instant instant = Instant.ofEpochSecond(unixTimeStamp);
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, UTC);
        return zonedDateTime.with(LocalTime.of(zonedDateTime.getHour(), 0)).toEpochSecond();
    }

    /**
     * Truncates the second from the passed time-stamp, i.e., the 09/10/2016 02:14:43 will be truncated to
     * 09/10/2016 02:14:00.
     *
     * @param unixTimeStamp the time-stamp to be truncated
     *
     * @return the truncated time-stamp
     */
    public static long toMinute(final long unixTimeStamp) {
        final Instant instant = Instant.ofEpochSecond(unixTimeStamp);
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, UTC);
        return zonedDateTime.with(LocalTime.of(zonedDateTime.getHour(), zonedDateTime.getMinute())).toEpochSecond();
    }
}
