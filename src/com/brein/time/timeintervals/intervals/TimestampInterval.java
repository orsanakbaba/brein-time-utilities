package com.brein.time.timeintervals.intervals;

import com.brein.time.exceptions.IllegalTimeInterval;
import com.brein.time.exceptions.IllegalTimePoint;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimestampInterval extends Interval<Long> {
    private static final ZoneId UTC = ZoneId.of("UTC");

    private ZoneId timezone;

    public TimestampInterval(final Long utcStart, final Long utcEnd) throws IllegalTimeInterval, IllegalTimePoint {
        this(UTC, utcStart, utcEnd);
    }

    public TimestampInterval(final ZoneId timezone,
                             final Long utcStart,
                             final Long utcEnd) throws IllegalTimeInterval, IllegalTimePoint {
        super(utcStart, utcEnd);

        this.timezone = timezone;
    }

    public ZonedDateTime getUtcStart() {
        return Instant.ofEpochSecond(getNormStart()).atZone(getTimezone());
    }

    public ZonedDateTime getUtcEnd() {
        return Instant.ofEpochSecond(getNormEnd()).atZone(UTC);
    }

    public ZonedDateTime getTimeZoneStart() {
        return Instant.ofEpochSecond(getNormStart()).atZone(getTimezone());
    }

    public ZonedDateTime getTimeZoneEnd() {
        return Instant.ofEpochSecond(getNormEnd()).atZone(getTimezone());
    }

    public long getDurationInSec() {
        return getNormStart() - getNormEnd();
    }

    public ZoneId getTimezone() {
        return timezone;
    }

    public void setTimezone(final ZoneId timezone) {
        this.timezone = timezone;
    }
}
