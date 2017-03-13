package com.brein.time.timeintervals.intervals;

import java.util.Objects;

public class IdInterval<I extends Comparable<I>, T extends Number & Comparable<T>> extends Interval<T> {
    private final I id;

    public IdInterval(final I id, final Long start, final Long end) {
        //noinspection unchecked
        this(id, Long.class, (T) start, (T) end, false, false);
    }

    public IdInterval(final I id, final Integer start, final Integer end) {
        //noinspection unchecked
        this(id, Integer.class, (T) start, (T) end, false, false);
    }

    public IdInterval(final I id, final Double start, final Double end) {
        //noinspection unchecked
        this(id, Double.class, (T) start, (T) end, false, false);
    }

    public IdInterval(final I id,
                      final Class clazz,
                      final T start,
                      final T end) {
        this(id, clazz, start, end, false, false);
    }

    public IdInterval(final I id,
                      final Class clazz,
                      final T start,
                      final T end,
                      final boolean openStart,
                      final boolean openEnd) {
        super(clazz, start, end, openStart, openEnd);
        this.id = id;
    }

    public I getId() {
        return id;
    }

    public boolean idEquals(final IdInterval i) {
        return i != null && Objects.equals(this.id, i.id);
    }

    @Override
    public IdInterval<I, T> clone() throws CloneNotSupportedException {
        return new IdInterval<>(getId(), getClazz(), getStart(), getEnd(), isOpenStart(), isOpenEnd());
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
        } else if (obj instanceof IInterval) {
            return super.equals(obj);
        } else {
            return false;
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "NullableProblems"})
    public int compareTo(final IInterval i) {
        if (i instanceof IdInterval) {
            final IdInterval iId = IdInterval.class.cast(i);
            final int cmp = super.compareTo(iId);

            if (cmp != 0 || Objects.equals(this.id, iId.id)) {
                return cmp;
            } else if (this.id.getClass().isInstance(iId.id)) {
                return this.id.compareTo((I) iId.id);
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
