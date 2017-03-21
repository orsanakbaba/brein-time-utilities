package com.brein.time.timeintervals.intervals;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class IdInterval<I extends Comparable<I> & Serializable, T extends Comparable<T> & Serializable>
        implements IInterval<T>, Externalizable {

    private I id;
    private IInterval<T> wrappedInterval;

    public IdInterval() {
        // just used for serialization
    }

    public IdInterval(final I id, final IInterval<T> wrappedInterval) {
        this.id = id;
        this.wrappedInterval = wrappedInterval;
    }

    public I getId() {
        return this.id;
    }

    @Override
    public T getNormStart() {
        return wrappedInterval.getNormStart();
    }

    @Override
    public T getNormEnd() {
        return wrappedInterval.getNormEnd();
    }

    @Override
    public String getUniqueIdentifier() {
        return wrappedInterval.getUniqueIdentifier();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (IInterval.class.isInstance(obj)) {
            return compareTo(IInterval.class.cast(obj)) == 0;
        } else {
            return false;
        }
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(final IInterval i) {
        final int cmp = this.wrappedInterval.compareTo(i);

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
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(this.id);
        out.writeObject(this.wrappedInterval);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = (I) in.readObject();
        this.wrappedInterval = (IInterval<T>) in.readObject();
    }

    @Override
    public String toString() {
        return String.format("%s@%s", this.id, this.wrappedInterval);
    }
}
