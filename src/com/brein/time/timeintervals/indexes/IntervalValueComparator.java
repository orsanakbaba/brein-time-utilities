package com.brein.time.timeintervals.indexes;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@FunctionalInterface
public interface IntervalValueComparator extends Serializable {
    List<Class<? extends Comparable<? extends Number>>> NUMBER_HIERARCHY = Arrays.asList(
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class
    );

    int compare(final Object o1, final Object o2);

    static int compareLongs(final Object o1, final Object o2) {
        if (!Long.class.isAssignableFrom(o1.getClass()) || !Long.class.isAssignableFrom(o2.getClass())) {
            throw new IllegalArgumentException(String.format("The values '%s (%s)' and '%s (%s)' " +
                    "are not comparable.", o1, o1.getClass(), o2, o2.getClass()));
        } else {
            return Long.class.cast(o1).compareTo(Long.class.cast(o2));
        }
    }

    static int compareInts(final Object o1, final Object o2) {
        if (!Integer.class.isAssignableFrom(o1.getClass()) || !Integer.class.isAssignableFrom(o2.getClass())) {
            throw new IllegalArgumentException(String.format("The values '%s (%s)' and '%s (%s)' " +
                    "are not comparable.", o1, o1.getClass(), o2, o2.getClass()));
        } else {
            return Integer.class.cast(o1).compareTo(Integer.class.cast(o2));
        }
    }

    static int compareDoubles(final Object o1, final Object o2) {
        if (!Double.class.isAssignableFrom(o1.getClass()) || !Double.class.isAssignableFrom(o2.getClass())) {
            throw new IllegalArgumentException(String.format("The values '%s (%s)' and '%s (%s)' " +
                    "are not comparable.", o1, o1.getClass(), o2, o2.getClass()));
        } else {
            return Double.class.cast(o1).compareTo(Double.class.cast(o2));
        }
    }

    static int compareNumbers(final Object o1, final Object o2) {

        if (!Number.class.isAssignableFrom(o1.getClass()) || !Number.class.isAssignableFrom(o2.getClass())) {
            throw new IllegalArgumentException(String.format("The values '%s (%s)' and '%s (%s)' " +
                    "are not comparable.", o1, o1.getClass(), o2, o2.getClass()));
        } else if (Comparable.class.isAssignableFrom(o1.getClass()) && o1.getClass().equals(o2.getClass())) {
            //noinspection unchecked
            return Comparable.class.cast(o1).compareTo(o2);
        } else {
            final int pos = Stream.of(o1.getClass(), o2.getClass())
                    .map(NUMBER_HIERARCHY::indexOf)
                    .filter(idx -> idx != -1)
                    .mapToInt(idx -> idx)
                    .max()
                    .orElse(-1);
            final Class<? extends Comparable> mappedClazz = NUMBER_HIERARCHY.get(pos);

            final Comparable mappedO1 = mapNumberValue(o1, mappedClazz);
            final Comparable mappedO2 = mapNumberValue(o2, mappedClazz);

            //noinspection unchecked
            return mappedO1.compareTo(mappedO2);
        }
    }

    @SuppressWarnings("unchecked")
    static <C> C mapNumberValue(final Object val, final Class<C> clazz) {

        // null is always mappable
        if (val == null) {
            return null;
        }

        // this implementation can only map number values
        final Number nrVal = Number.class.cast(val);
        if (Short.class.equals(clazz)) {
            return (C) Short.valueOf(nrVal.shortValue());
        } else if (Byte.class.equals(clazz)) {
            return (C) Byte.valueOf(nrVal.byteValue());
        } else if (Integer.class.equals(clazz)) {
            return (C) Integer.valueOf(nrVal.intValue());
        } else if (Long.class.equals(clazz)) {
            return (C) Long.valueOf(nrVal.longValue());
        } else if (Float.class.equals(clazz)) {
            return (C) Float.valueOf(nrVal.floatValue());
        } else if (Double.class.equals(clazz)) {
            return (C) Double.valueOf(nrVal.doubleValue());
        } else {
            throw new IllegalArgumentException("The class '" + clazz + "' is not supported.");
        }
    }
}
