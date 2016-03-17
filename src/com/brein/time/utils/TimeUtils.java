package com.brein.time.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
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
}
