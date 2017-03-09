package com.brein.time.timeintervals.indexes;

public enum IntervalTreeNodeChildType {
    LEFT,
    RIGHT,
    NONE;

    public IntervalTreeNodeChildType flip() {
        if (IntervalTreeNodeChildType.LEFT.equals(this)) {
            return RIGHT;
        } else if (IntervalTreeNodeChildType.RIGHT.equals(this)) {
            return LEFT;
        } else {
            return null;
        }
    }
}
