package com.brein.time.timeintervals.intervals;

import com.brein.time.exceptions.IllegalTimeInterval;
import com.brein.time.exceptions.IllegalTimePoint;
import com.brein.time.timeintervals.indexes.IntervalValueComparator;
import org.apache.log4j.Logger;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberInterval<T extends Number & Comparable<T>> implements IInterval<T>, Externalizable {
    private static final Logger LOGGER = Logger.getLogger(NumberInterval.class);
    private static final double MAX_DOUBLE = Math.pow(2, 54) - 2;

    private Class clazz;

    private T start;
    private T end;

    private boolean openStart;
    private boolean openEnd;

    public NumberInterval() {
        // just for de- and serialization
    }

    public NumberInterval(final Class<T> clazz,
                          final T start,
                          final T end) throws IllegalTimeInterval, IllegalTimePoint {
        this(clazz, start, end, false, false);
    }

    public NumberInterval(final Class<T> clazz,
                          final T start,
                          final T end,
                          final boolean openStart,
                          final boolean openEnd) throws IllegalTimeInterval, IllegalTimePoint {
        this.clazz = clazz;
        init(start, end, openStart, openEnd);
    }

    @SuppressWarnings("unchecked")
    protected void init(final Object start,
                        final Object end,
                        final boolean openStart,
                        final boolean openEnd) throws IllegalTimeInterval, IllegalTimePoint {
        this.start = validate(start, true);
        this.end = validate(end, false);

        this.openStart = openStart;
        this.openEnd = openEnd;

        if (compare(getNormEnd(), getNormStart()) < 0) {
            throw new IllegalTimeInterval("The end value '" + end + "' " +
                    "cannot be smaller than the start value '" + start + "'.");
        }
    }

    @SuppressWarnings("unchecked")
    protected T validate(final Object val, final boolean start) throws IllegalTimeInterval, IllegalTimePoint {
        final T result;

        if (val == null) {
            return start ? determineMinValue() : determineMaxValue();
        } else if (this.clazz.isInstance(val)) {
            result = (T) this.clazz.cast(val);
        } else {
            result = (T) IntervalValueComparator.mapNumberValue(val, this.clazz);
        }

        final T min = determineMinValue();
        final T max = determineMaxValue();
        if (min.equals(result) || max.equals(result)) {
            throw new IllegalTimeInterval("The minimal and maximal value are reserved.");
        } else if (getNextValue(min).equals(result) || getPreviousValue(max).equals(result)) {
            throw new IllegalTimeInterval("The edge values are reserved and cannot be used.");
        } else if (Double.class.equals(this.clazz)) {
            final Double dVal = Double.class.cast(result);
            if (dVal.equals(Double.NaN)) {
                throw new IllegalTimePoint("The value NaN is a not supported value.");
            } else if (dVal.equals(Double.NEGATIVE_INFINITY)) {
                return determineMinValue();
            } else if (dVal.equals(Double.POSITIVE_INFINITY)) {
                return determineMaxValue();
            }
        } else if (Float.class.equals(this.clazz)) {
            final Float fVal = Float.class.cast(result);
            if (fVal.equals(Float.NaN)) {
                throw new IllegalTimePoint("The value NaN is a not supported value.");
            } else if (fVal.equals(Float.NEGATIVE_INFINITY)) {
                return determineMinValue();
            } else if (fVal.equals(Float.POSITIVE_INFINITY)) {
                return determineMaxValue();
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    protected T determineMaxValue() {
        if (Short.class.equals(this.clazz)) {
            return (T) Short.valueOf(Short.MAX_VALUE);
        } else if (Byte.class.equals(this.clazz)) {
            return (T) Byte.valueOf(Byte.MAX_VALUE);
        } else if (Integer.class.equals(this.clazz)) {
            return (T) Integer.valueOf(Integer.MAX_VALUE);
        } else if (Long.class.equals(this.clazz)) {
            return (T) Long.valueOf(Long.MAX_VALUE);
        } else if (Float.class.equals(this.clazz)) {
            return (T) Float.valueOf(Float.MAX_VALUE);
        } else if (Double.class.equals(this.clazz)) {
            return (T) Double.valueOf(Double.MAX_VALUE);
        } else {
            throw new IllegalArgumentException("The class '" + this.clazz + "' is not supported.");
        }
    }

    @SuppressWarnings("unchecked")
    protected T determineMinValue() {
        if (Short.class.equals(this.clazz)) {
            return (T) Short.valueOf(Short.MIN_VALUE);
        } else if (Byte.class.equals(this.clazz)) {
            return (T) Byte.valueOf(Byte.MIN_VALUE);
        } else if (Integer.class.equals(this.clazz)) {
            return (T) Integer.valueOf(Integer.MIN_VALUE);
        } else if (Long.class.equals(this.clazz)) {
            return (T) Long.valueOf(Long.MIN_VALUE);
        } else if (Float.class.equals(this.clazz)) {
            return (T) Float.valueOf(Float.MIN_VALUE);
        } else if (Double.class.equals(this.clazz)) {
            return (T) Double.valueOf(Double.MIN_VALUE);
        } else {
            throw new IllegalArgumentException("The class '" + this.clazz + "' is not supported.");
        }
    }

    @SuppressWarnings("unchecked")
    protected T getNextValue(final T val) {
        if (determineMinValue().equals(val) || determineMaxValue().equals(val)) {
            return val;
        }

        if (Short.class.equals(this.clazz)) {
            return (T) Short.valueOf((short) (Short.class.cast(val) + 1));
        } else if (Byte.class.equals(this.clazz)) {
            return (T) Byte.valueOf((byte) (Byte.class.cast(val) + 1));
        } else if (Integer.class.equals(this.clazz)) {
            return (T) Integer.valueOf(Integer.class.cast(val) + 1);
        } else if (Long.class.equals(this.clazz)) {
            return (T) Long.valueOf(Long.class.cast(val) + 1L);
        } else if (Float.class.equals(this.clazz)) {
            return (T) Double.valueOf(Math.nextAfter(Float.class.cast(val), Double.POSITIVE_INFINITY));
        } else if (Double.class.equals(this.clazz)) {
            return (T) Double.valueOf(Math.nextAfter(Double.class.cast(val), Double.POSITIVE_INFINITY));
        } else {
            throw new IllegalArgumentException("The class '" + clazz + "' is not supported.");
        }
    }

    @SuppressWarnings("unchecked")
    protected T getPreviousValue(final T val) {
        if (determineMinValue().equals(val) || determineMaxValue().equals(val)) {
            return val;
        }

        if (Short.class.equals(this.clazz)) {
            return (T) Short.valueOf((short) (Short.class.cast(val) - 1));
        } else if (Byte.class.equals(this.clazz)) {
            return (T) Byte.valueOf((byte) (Byte.class.cast(val) - 1));
        } else if (Integer.class.equals(this.clazz)) {
            return (T) Integer.valueOf(Integer.class.cast(val) - 1);
        } else if (Long.class.equals(this.clazz)) {
            return (T) Long.valueOf(Long.class.cast(val) - 1L);
        } else if (Float.class.equals(this.clazz)) {
            return (T) Double.valueOf(Math.nextAfter(Float.class.cast(val), Double.NEGATIVE_INFINITY));
        } else if (Double.class.equals(this.clazz)) {
            return (T) Double.valueOf(Math.nextAfter(Double.class.cast(val), Double.NEGATIVE_INFINITY));
        } else {
            throw new IllegalArgumentException("The class '" + clazz + "' is not supported.");
        }
    }

    public T getStart() {
        return start;
    }

    @Override
    public T getNormStart() {
        return norm(this.start, this.openStart, true);
    }

    @Override
    public T getNormEnd() {
        return norm(this.end, this.openEnd, false);
    }

    @Override
    public String getUniqueIdentifier() {
        return "[" + unique(getNormStart()) + "," + unique(getNormEnd()) + "]";
    }

    public T getEnd() {
        return end;
    }

    protected String unique(final T value) {
        if (Short.class.equals(this.clazz)) {
            return String.valueOf(Short.class.cast(value).longValue());
        } else if (Byte.class.equals(this.clazz)) {
            return String.valueOf(Byte.class.cast(value).longValue());
        } else if (Integer.class.equals(this.clazz)) {
            return String.valueOf(Integer.class.cast(value).longValue());
        } else if (Long.class.equals(this.clazz)) {
            return String.valueOf(Long.class.cast(value).longValue());
        } else if (Float.class.equals(this.clazz)) {
            return unique(Float.class.cast(value).doubleValue());
        } else if (Double.class.equals(this.clazz)) {
            return unique(Double.class.cast(value));
        } else {
            throw new IllegalArgumentException("The class '" + this.clazz + "' is not supported.");
        }
    }

    protected String unique(final double value) {
        if (MAX_DOUBLE < Math.abs(value)) {
            LOGGER.warn("Using double values larger than " + unique(MAX_DOUBLE));
        }

        if (value == Math.rint(value)) {
            return String.valueOf(Double.valueOf(value).longValue());
        } else {
            // http://stackoverflow.com/questions/16098046/
            // how-to-print-double-value-without-scientific-notation-using-java
            final DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            df.setMaximumFractionDigits(340);

            return df.format(value);
        }
    }

    public boolean isOpenStart() {
        return openStart;
    }

    public boolean isOpenEnd() {
        return openEnd;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getClazz() {
        return clazz;
    }

    public boolean overlaps(final IInterval interval) {
        final int startCmp = compare(getNormStart(), interval.getNormEnd());
        if (startCmp > 0) {
            return false;
        } else if (startCmp == 0) {
            return true;
        }

        return compare(getNormEnd(), interval.getNormStart()) >= 0;
    }

    public AllenIntervalRelation ir(final IInterval interval) {
        return AllenIntervalRelation.determineRelation(this, interval);
    }

    public boolean irOverlaps(final IInterval interval) {
        return compare(getNormStart(), interval.getNormStart()) < 0 &&
                compare(getNormEnd(), interval.getNormEnd()) < 0;
    }

    public boolean irIsOverlappedBy(final IInterval interval) {
        return compare(interval.getNormStart(), getNormStart()) < 0 &&
                compare(interval.getNormEnd(), getNormEnd()) < 0;
    }

    public boolean irEquals(final IInterval interval) {
        return compare(getNormStart(), interval.getNormStart()) == 0 &&
                compare(getNormEnd(), interval.getNormEnd()) == 0;
    }

    public boolean irBegins(final IInterval interval) {
        return compare(getNormStart(), interval.getNormStart()) == 0 &&
                compare(getNormEnd(), interval.getNormEnd()) > 0;
    }

    public boolean irBeginsBy(final IInterval interval) {
        return compare(getNormStart(), interval.getNormStart()) == 0 &&
                compare(getNormEnd(), interval.getNormEnd()) < 0;
    }

    public boolean irEnds(final IInterval interval) {
        return compare(getNormStart(), interval.getNormStart()) < 0 &&
                compare(getNormEnd(), interval.getNormEnd()) == 0;
    }

    public boolean irEndsBy(final IInterval interval) {
        return compare(getNormStart(), interval.getNormStart()) > 0 &&
                compare(getNormEnd(), interval.getNormEnd()) == 0;
    }

    public boolean irBefore(final IInterval interval) {
        return compare(getNormEnd(), interval.getNormStart()) < 0 &&
                !irEndsDirectlyBefore(interval);
    }

    public boolean irAfter(final IInterval interval) {
        return compare(getNormStart(), interval.getNormEnd()) > 0 &&
                !irStartsDirectlyBefore(interval);
    }

    public boolean irIncludes(final IInterval interval) {
        return compare(getNormStart(), interval.getNormStart()) < 0 &&
                compare(getNormEnd(), interval.getNormEnd()) > 0;
    }

    public boolean irIsDuring(final IInterval interval) {
        return compare(getNormStart(), interval.getNormStart()) > 0 &&
                compare(getNormEnd(), interval.getNormEnd()) < 0;
    }

    public boolean irStartsDirectlyBefore(final IInterval interval) {
        return compare(interval.getNormEnd(), getPreviousValue(getNormStart())) == 0;
    }

    public boolean irEndsDirectlyBefore(final IInterval interval) {
        return compare(getNextValue(getNormEnd()), interval.getNormStart()) == 0;
    }

    @Override
    public int hashCode() {
        return getUniqueIdentifier().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof NumberInterval) {
            final NumberInterval i = NumberInterval.class.cast(obj);
            return compareTo(i) == 0;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("%s%s, %s%s", getStartMarker(), getStart(), getEnd(), getEndMarker());
    }

    protected String getStartMarker() {
        return isOpenStart() ? "(" : "[";
    }

    protected String getEndMarker() {
        return isOpenEnd() ? ")" : "]";
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(final IInterval i) {
        final int cmpStart = compare(getNormStart(), i.getNormStart());
        if (cmpStart == 0) {
            return compare(getNormEnd(), i.getNormEnd());
        } else {
            return cmpStart;
        }
    }

    public boolean contains(final Object value) {
        return compare(getNormStart(), value) <= 0 && compare(getNormEnd(), value) >= 0;
    }

    protected T norm(final T val, final boolean open, final boolean start) {
        if (start) {
            return open ? getNextValue(val) : val;
        } else {
            return open ? getPreviousValue(val) : val;
        }
    }

    protected int compare(final Object o1, final Object o2) {
        return IntervalValueComparator.compareNumbers(o1, o2);
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(clazz);
        out.writeObject(start);
        out.writeObject(end);
        out.writeBoolean(openStart);
        out.writeBoolean(openEnd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.clazz = Class.class.cast(in.readObject());
        this.start = (T) this.clazz.cast(in.readObject());
        this.end = (T) this.clazz.cast(in.readObject());
        this.openStart = in.readBoolean();
        this.openEnd = in.readBoolean();
    }
}
