package com.brein.time.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {

    public static String format(final long unixTimeStamp) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return sdf.format(new Date(unixTimeStamp * 1000L));
    }

    public static long now() {
        return Instant.now().getEpochSecond();
    }

    public static Calendar createFirstOfMonthCal(final long unixTimestamp) {
        /*
         * Calendar set method doesn't know that Calendar.get(...) will return one of the months it expects. Thus, we
         * have to ignore the evaluation of MagicConstant.
         */
        final Calendar nowCalendar = createCal(unixTimestamp);
        final Calendar firstOfMonthCal = createCal();
        //noinspection MagicConstant
        firstOfMonthCal.set(nowCalendar.get(Calendar.YEAR), nowCalendar.get(Calendar.MONTH), 1, 0, 0, 0);

        return firstOfMonthCal;
    }

    public static Calendar createCal() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }

    public static Calendar createCal(final long unixTimeStamp) {
        final Calendar cal = createCal();
        cal.setTimeInMillis(unixTimeStamp * 1000L);

        return cal;
    }

    public static long firstOfNextMonthTime(final long unixTimestamp) {
        final Calendar cal = createFirstOfMonthCal(unixTimestamp);
        cal.add(Calendar.MONTH, 1);

        return cal.getTimeInMillis() / 1000L;
    }

    public static long firstOfLastMonthTime(final long unixTimestamp) {
        final Calendar cal = createFirstOfMonthCal(unixTimestamp);
        cal.add(Calendar.MONTH, -1);

        return cal.getTimeInMillis() / 1000L;
    }

    public static boolean isSameMonth(final long unixTimestamp1,
                                      final long unixTimestamp2) {
        final Calendar cal1 = createCal(unixTimestamp1);
        final Calendar cal2 = createCal(unixTimestamp2);
        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
                && (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH));
    }
}
