package com.brein.time.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtils {
    private static final ZoneId UTC = ZoneId.of("UTC");

    public static String format(final long unixTimeStamp) {
        return formatUnixTimeStamp("yyyy-MM-dd HH:mm:ss z", unixTimeStamp);
    }

    public static String formatUnixTimeStamp(final long unixTimeStamp) {
        return formatUnixTimeStamp("yyyy/MM/dd HH:mm:ss", unixTimeStamp);
    }

    public static String formatUnixTimeStamp(final String format, final long unixTimeStamp) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return Instant.ofEpochSecond(unixTimeStamp)
                .atZone(UTC)
                .format(formatter);
    }

    public static long now() {
        return Instant.now().getEpochSecond();
    }

    public static int secondsToFullMinute(final long now) {
        return secondsToFullMinute(now, 0);
    }

    /**
     * Calculate the rest of seconds to a full minute, i.e. at 10:44:14 the method returns 46.
     *
     * @param now the current time
     *
     * @return the seconds until the full minute
     */
    public static int secondsToFullMinute(final long now, final int offset) {
        final int value = Long.valueOf(60 - ((now + offset) % 60)).intValue();
        return value == 60 ? 0 : value;
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

    public static boolean validateDate(final String day,
                                       final String month,
                                       final String year) {

        if (day == null || month == null || year == null) {
            return false;
        }

        final String format = "dd-MM-uuuu";
        final String date = String.format("%02d-%02d-%s", Integer.parseInt(day), Integer.parseInt(month), year);

        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT));
            return true;
        } catch (final DateTimeParseException e) {
            return false;
        }
    }
}
