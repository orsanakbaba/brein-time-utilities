package com.brein.time.timeintervals.intervals;

import java.util.Objects;

public class IdInterval<T extends Cloneable> extends Interval {
    private final T id;

    public IdInterval(final T id,
                      final long start,
                      final long end) {
        this(id, start, end, false, false);
    }

    public IdInterval(final T id,
                      final long start,
                      final long end,
                      final boolean openStart,
                      final boolean openEnd) {
        super(start, end, openStart, openEnd);
        this.id = id;
    }

    public T getId() {
        return id;
    }

    public boolean idEquals(final IdInterval i) {
        return i != null && Objects.equals(this.id, i.id);
    }

    @Override
    public IdInterval<T> clone() throws CloneNotSupportedException {
        return new IdInterval<>(getId(), this.getStart(), this.getEnd(), this.isOpenStart(), this.isOpenEnd());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof IdInterval) {
            final IdInterval i = IdInterval.class.cast(obj);
            return Objects.equals(this.id, i.id) && super.equals(i);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
