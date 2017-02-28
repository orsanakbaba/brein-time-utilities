package com.brein.time.utils;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class TimeUtils {
    private static final Logger LOGGER = Logger.getLogger(TimeUtils.class);

    public static final ZoneId UTC = ZoneId.of("UTC");
    public static final Map<String, ZoneId> ZONES = new HashMap<>();

    // fill the zones
    static {
        ZoneId.getAvailableZoneIds().stream()
                .map(ZoneId::of)
                .forEach(zoneId -> ZONES.put(zoneId.getId().toLowerCase(), zoneId));
    }

    public static String format(final long unixTimeStamp) {
        return formatUnixTimeStamp("yyyy-MM-dd HH:mm:ss z", unixTimeStamp);
    }

    public static String formatUnixTimeStamp(final long unixTimeStamp) {
        return formatUnixTimeStamp("yyyy/MM/dd HH:mm:ss", unixTimeStamp);
    }

    public static String formatUnixTimeStamp(final String format, final long unixTimeStamp) {
        return format(format, unixTimeStamp, UTC);
    }

    public static String format(final String format, final long unixTimeStamp, final ZoneId zone) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return Instant.ofEpochSecond(unixTimeStamp)
                .atZone(zone)
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

    /**
     * @param unixTimestamp Current time
     *
     * @return Timestamp for the beginning of the previous month relative to current time
     */
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

    public static ZonedDateTime toZone(final long utc, final String zoneId) {
        return toZone(utc, zoneId(zoneId));
    }

    public static ZonedDateTime toZone(final long utc, final ZoneId toZone) {
        final Instant instant = Instant.ofEpochSecond(utc);
        final ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, UTC);

        return toZone(zdt, toZone);
    }

    public static ZonedDateTime toZone(final ZonedDateTime utc, final String zoneId) {
        return toZone(utc, zoneId(zoneId));
    }

    public static ZonedDateTime toZone(final ZonedDateTime utc, final ZoneId toZone) {
        if (!UTC.equals(utc.getZone())) {
            throw new IllegalArgumentException("Expecting UTC time.");
        }

        return utc.withZoneSameInstant(toZone);
    }

    public static ZoneId zoneId(final String tzId) {
        return zoneId(tzId, true);
    }

    public static ZoneId zoneId(final String tzId, final boolean caseSensitive) {
        if (tzId == null) {
            return null;
        } else if (caseSensitive) {
            try {
                return ZoneId.of(tzId);
            } catch (final Exception e) {
                return null;
            }
        } else {
            return ZONES.get(tzId.toLowerCase());
        }
    }

    /**
     * Converts a time to it's midnight
     *
     * @param time the base time in seconds
     *
     * @return the midnight's time
     */
    public static long toMidnight(final long time) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time * 1000);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTimeInMillis() / 1000;
    }

    /**
     * Converts specified date string with given format to a unix timestamp. Returns -1 if there was a failure
     *
     * @param dateString Date string value
     * @param format     Format that date string is in
     *
     * @return Unix timestamp, or -1 if something went wrong
     */
    public static long dateStringToUnixTimestamp(final String dateString,
                                                 final String format) {
        return dateStringToUnixTimestamp(dateString, format, null);
    }

    public static long dateStringToUnixTimestamp(final String dateString,
                                                 final String format,
                                                 final String timezone) {
        if (dateString == null || dateString.isEmpty()) {
            return -1;
        }

        final DateFormat dateFormat = new SimpleDateFormat(format);
        if (timezone != null) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        }

        try {
            final Date parsedDate = dateFormat.parse(dateString);
            if (parsedDate == null) {
                return -1;
            } else {
                return parsedDate.getTime() / 1000L;
            }
        } catch (final ParseException e) {
            LOGGER.error("Unable to parse date '" + dateString + "' with format: " + format);
            return -1;
        }
    }

    public static String convertDateFormat(final String dateString,
                                           final String fromFormat,
                                           final String toFormat) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat);
        try {
            final Date date = dateFormat.parse(dateString);
            dateFormat.applyPattern(toFormat);
            return dateFormat.format(date);
        } catch (final ParseException e) {
            LOGGER.error("Unable to parse date format: " + fromFormat);
            return null;
        }
    }

    public static String secondsToPrettyString(final int seconds) {
        if (seconds < 0) {
            return "-" + secondsToPrettyString(-seconds);
        } else {
            final double converted;
            final String type;
            if (seconds >= 2 * 365 * 24 * 60 * 60) {
                converted = seconds / 365 / 24 / 60 / 60.0;
                type = "years";
            } else if (seconds >= 2 * 7 * 24 * 60 * 60) {
                converted = seconds / 7 / 24 / 60 / 60.0;
                type = "weeks";
            } else if (seconds >= 2 * 24 * 60 * 60) {
                converted = seconds / 24 / 60 / 60.0;
                type = "days";
            } else if (seconds >= 2 * 60 * 60) {
                converted = seconds / 60 / 60.0;
                type = "hours";
            } else if (seconds >= 2 * 60) {
                converted = seconds / 60.0;
                type = "minutes";
            } else if (seconds == 1) {
                converted = 1;
                type = "second";
            } else {
                converted = seconds;
                type = "seconds";
            }
            return Math.round(converted) + " " + type;
        }
    }
}
