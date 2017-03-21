package com.brein.time.timeintervals.intervals;

public enum AllenIntervalRelation {

    OVERLAPS,
    IS_OVERLAPPED_BY,
    EQUALS,
    BEGINS,
    ENDS,
    BEGINS_BY,
    ENDS_BY,
    BEFORE,
    AFTER,
    INCLUDES,
    IS_DURING,
    STARTS_DIRECTLY_BEFORE,
    ENDS_DIRECTLY_BEFORE;

    public static AllenIntervalRelation determineRelation(final NumberInterval i1, final IInterval i2) {

        if (i1.irOverlaps(i2)) {
            return OVERLAPS;
        } else if (i1.irIsOverlappedBy(i2)) {
            return IS_OVERLAPPED_BY;
        } else if (i1.irEquals(i2)) {
            return EQUALS;
        } else if (i1.irBegins(i2)) {
            return BEGINS;
        } else if (i1.irBeginsBy(i2)) {
            return BEGINS_BY;
        } else if (i1.irEnds(i2)) {
            return ENDS;
        } else if (i1.irEndsBy(i2)) {
            return ENDS_BY;
        } else if (i1.irBefore(i2)) {
            return BEFORE;
        } else if (i1.irAfter(i2)) {
            return AFTER;
        } else if (i1.irStartsDirectlyBefore(i2)) {
            return STARTS_DIRECTLY_BEFORE;
        } else if (i1.irEndsDirectlyBefore(i2)) {
            return ENDS_DIRECTLY_BEFORE;
        } else if (i1.irIncludes(i2)) {
            return INCLUDES;
        } else if (i1.irIsDuring(i2)) {
            return IS_DURING;
        } else {
            // this should never happen
            return null;
        }
    }
}