package com.brein.time.utils;

import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * @deprecated since 1.6.2, please use {@link TimeModifier} instead.
 */
@Deprecated
public class TimeTruncater {
    private static final ZoneId UTC = ZoneOffset.UTC;

    /**
     * Truncates the passed time-stamp to the first of the month information only, i.e., the 09/10/2016 00:14:43 will be
     * truncated to 01/10/2016 00:00:00.
     *
     * @param unixTimeStamp the time-stamp to be truncated
     *
     * @return the date information only
     *
     * @deprecated since 1.6.2, use {@link TimeModifier#START_OF_MONTH}
     */
    @Deprecated
    public static long toMonth(final long unixTimeStamp) {
        return TimeModifier.START_OF_MONTH.applyModifier(unixTimeStamp);
    }

    /**
     * Truncates the passed time-stamp to the date information only, i.e., the 09/10/2016 00:14:43 will be truncated to
     * 09/10/2016 00:00:00.
     *
     * @param unixTimeStamp the time-stamp to be truncated
     *
     * @return the date information only
     *
     * @deprecated since 1.6.2, use {@link TimeModifier#START_OF_DAY}
     */
    @Deprecated
    public static long toDay(final long unixTimeStamp) {
        return TimeModifier.START_OF_DAY.applyModifier(unixTimeStamp);
    }

    /**
     * @deprecated since 1.6.2, use {@link TimeModifier#END_OF_DAY}
     */
    @Deprecated
    public static long toEndOfDay(final long unixTimeStamp) {
        return TimeModifier.END_OF_DAY.applyModifier(unixTimeStamp);
    }

    /**
     * Truncates the minute and second from the passed time-stamp, i.e., the 09/10/2016 02:14:43 will be truncated to
     * 09/10/2016 02:00:00.
     *
     * @param unixTimeStamp the time-stamp to be truncated
     *
     * @return the truncated time-stamp
     *
     * @deprecated since 1.6.2, use {@link TimeModifier#START_OF_HOUR}
     */
    @Deprecated
    public static long toHour(final long unixTimeStamp) {
        return TimeModifier.START_OF_HOUR.applyModifier(unixTimeStamp);
    }

    /**
     * Truncates the second from the passed time-stamp, i.e., the 09/10/2016 02:14:43 will be truncated to
     * 09/10/2016 02:14:00.
     *
     * @param unixTimeStamp the time-stamp to be truncated
     *
     * @return the truncated time-stamp
     *
     * @deprecated since 1.6.2, use {@link TimeModifier#START_OF_MINUTE}
     */
    @Deprecated
    public static long toMinute(final long unixTimeStamp) {
        return TimeModifier.START_OF_MINUTE.applyModifier(unixTimeStamp);
    }
}
