package com.brein.time.utils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public enum TimeModifier {
    START_OF_DAY,
    END_OF_DAY,
    NONE;

    public ZonedDateTime applyModifier(final ZonedDateTime dateTime) {
        if (START_OF_DAY.equals(this)) {
            return dateTime.truncatedTo(ChronoUnit.DAYS);
        } else if (END_OF_DAY.equals(this)) {
            return dateTime
                    .truncatedTo(ChronoUnit.DAYS)
                    .plusDays(1)
                    .minusSeconds(1);
        } else if (NONE.equals(this)) {
            return dateTime;
        } else {
            throw new IllegalArgumentException("Unexpected TimeModifier: " + this);
        }
    }

    /**
     * A unix timestamp to apply the modification to.
     *
     * @param timestamp the timestamp to apply the modification to
     *
     * @return the modified unix timestamp
     */
    public long applyModifier(final long timestamp) {
        return this.applyModifier(TimeUtils.toZone(timestamp, TimeUtils.UTC)).toEpochSecond();
    }
}
