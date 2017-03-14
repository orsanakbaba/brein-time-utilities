package com.brein.time.timeintervals.intervals;

public class IdInterval<I extends Comparable<I>, T extends Number & Comparable<T>> extends Interval<T> {
    private final I id;

    public IdInterval(final I id, final Long start, final Long end) {
        super(start, end);
        this.id = id;
    }

    public IdInterval(final I id, final Integer start, final Integer end) {
        super(start, end);
        this.id = id;
    }

    public IdInterval(final I id, final Double start, final Double end) {
        super(start, end);
        this.id = id;
    }

    public IdInterval(final I id,
                      final Class<T> clazz,
                      final T start,
                      final T end) {
        this(id, clazz, start, end, false, false);
    }

    public IdInterval(final I id,
                      final Class<T> clazz,
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

    public int compareId(final IdInterval iId) {
        if (this.id == null && iId == null) {
            return 0;
        } else if (this.id == null) {
            return -1;
        } else if (iId == null) {
            return 1;
        } else if (this.id.getClass().isInstance(iId.id)) {
            //noinspection unchecked
            return this.id.compareTo((I) iId.id);
        } else {
            return this.id.toString().compareTo(iId.id.toString());
        }
    }

    @Override
    public IdInterval<I, T> clone() throws CloneNotSupportedException {
        return new IdInterval<>(getId(), getClazz(), getStart(), getEnd(), isOpenStart(), isOpenEnd());
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(final IInterval i) {
        final int cmp = super.compareTo(i);

        if (cmp == 0) {

            // the intervals are equal, so we must use the identifiers
            if (i instanceof IdInterval) {
                return compareId(IdInterval.class.cast(i));
            }
            // we don't have any identifiers (the instance is of a different type)
            else {
                return getClass().getName().compareTo(i.getClass().getName());
            }
        } else {
            return cmp;
        }
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
