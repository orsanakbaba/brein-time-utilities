package com.brein.time.timeintervals.intervals;

import com.brein.time.exceptions.IllegalTimeInterval;

import java.util.Objects;

public class Interval implements Comparable<Interval>, Cloneable {
    private final long start;
    private final long end;

    private final boolean openStart;
    private final boolean openEnd;

    public Interval(final Long start,
                    final Long end) {
        this(start, end, false, false);
    }

    public Interval(final Long start,
                    final Long end,
                    final boolean openStart,
                    final boolean openEnd) throws IllegalTimeInterval {
        this.start = validate(start, true);
        this.end = validate(end, false);

        this.openStart = openStart;
        this.openEnd = openEnd;

        if (getNormEnd() < getNormStart()) {
            throw new IllegalTimeInterval("The end value cannot be smaller than the start value.");
        }
    }

    protected long validate(final Long val, final boolean start) throws IllegalTimeInterval {
        if (val == null) {
            return start ? Long.MIN_VALUE : Long.MAX_VALUE;
        } else if (val == Long.MIN_VALUE || val == Long.MAX_VALUE) {
            throw new IllegalTimeInterval("The value minimal and maximal value are reserved.");
        } else {
            return val;
        }
    }

    public long getStart() {
        return start;
    }

    public long getNormStart() {
        return norm(this.start, this.openStart, true);
    }

    public long getEnd() {
        return end;
    }

    public long getNormEnd() {
        return norm(this.end, this.openEnd, false);
    }

    public boolean isOpenStart() {
        return openStart;
    }

    public boolean isOpenEnd() {
        return openEnd;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof Interval) {
            final Interval i = Interval.class.cast(obj);
            return compareTo(i) == 0;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.start, this.end);
    }

    @Override
    public String toString() {
        return String.format("%s%s, %s%s", getStartMarker(), getStart(), getEnd(), getEndMarker());
    }

    public Interval getNormalized() {
        return new Interval(getNormStart(), getNormEnd());
    }

    @Override
    public Interval clone() throws CloneNotSupportedException {
        return new Interval(getStart(), getEnd(), isOpenStart(), isOpenEnd());
    }

    protected String getStartMarker() {
        return isOpenStart() ? "(" : "[";
    }

    protected String getEndMarker() {
        return isOpenEnd() ? ")" : "]";
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(final Interval i) {
        final int cmpStart = cmp(this.start, i.start, this.openStart, i.openStart, true);
        if (cmpStart == 0) {
            return cmp(this.end, i.end, this.openEnd, i.openEnd, false);
        } else {
            return cmpStart;
        }
    }

    protected int cmp(final long val1, final long val2,
                      final boolean open1, final boolean open2,
                      final boolean start) {
        final long normVal1 = norm(val1, open1, start);
        final long normVal2 = norm(val2, open2, start);

        return Long.compare(normVal1, normVal2);
    }

    protected long norm(final long val, final boolean open, final boolean start) {
        if (start) {
            return val + (val == Long.MIN_VALUE || !open ? 0L : 1L);
        } else {
            return val - (val == Long.MAX_VALUE || !open ? 0L : 1L);
        }
    }
}
