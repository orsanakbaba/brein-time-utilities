package com.brein.time.timeintervals.intervals;

import java.util.Objects;

public class IdInterval<T extends Comparable<T>> extends Interval {
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
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof IdInterval) {
            final IdInterval iId = IdInterval.class.cast(obj);
            return Objects.equals(this.id, iId.id) && super.equals(iId);
        } else if (obj instanceof Interval) {
            return super.equals(obj);
        } else {
            return false;
        }
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(final Interval i) {
        if (i instanceof IdInterval) {
            final IdInterval<?> iId = IdInterval.class.cast(i);
            final int cmp = super.compareTo(iId);

            if (cmp != 0 || Objects.equals(this.id, iId.id)) {
                return cmp;
            } else if (this.id.getClass().isInstance(iId.id)) {
                return this.id.compareTo((T) iId.id);
            } else {
                return this.id.toString().compareTo(iId.id.toString());
            }
        } else {
            final int cmp = super.compareTo(i);

            // if they are equal they cannot be, because there is no identifier, so we can only be less or more
            if (cmp == 0) {

                // we don't have any empty identifier, thus we compare the class-names
                return this.getClass().getName().compareTo(i.getClass().getName());
            } else {
                return cmp;
            }
        }
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
