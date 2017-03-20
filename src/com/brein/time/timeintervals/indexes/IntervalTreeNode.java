package com.brein.time.timeintervals.indexes;

import com.brein.time.timeintervals.collections.IntervalCollection;
import com.brein.time.timeintervals.collections.IntervalCollection.IntervalFilter;
import com.brein.time.timeintervals.collections.IntervalCollection.IntervalFilters;
import com.brein.time.timeintervals.collections.IntervalCollectionFactory;
import com.brein.time.timeintervals.collections.IntervalCollectionObserver;
import com.brein.time.timeintervals.collections.ObservableIntervalCollection;
import com.brein.time.timeintervals.intervals.IInterval;
import org.apache.log4j.Logger;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

public class IntervalTreeNode extends IntervalTreeNodeContext
        implements Externalizable, Iterable<IInterval>, Comparable<IntervalTreeNode> {
    private static final Logger LOGGER = Logger.getLogger(IntervalTreeNode.class);

    private transient Comparator comparator;

    private IntervalCollection collection;

    private Comparable start;
    private Comparable end;

    private Comparable max;
    private long level;
    private long height;

    public IntervalTreeNode() {
        // just for de- and serialization
    }

    public IntervalTreeNode(final IInterval interval,
                            final IntervalCollection collection) {
        this.start = interval.getNormStart();
        this.end = interval.getNormEnd();
        this.max = interval.getNormEnd();

        this.comparator = interval.getComparator();
        this.level = 0L;
        this.height = 1L;

        this.collection = collection;

        if (!this.collection.isEmpty()) {
            LOGGER.warn("New IntervalTreeNode with filled collection, collection will be cleared: " + collection);
            this.collection.clear();
        }

        this.collection.add(interval);
    }

    public Object getStart() {
        return start;
    }

    public Object getEnd() {
        return end;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(final Comparable max) {
        if (compare(this.max, max) == 0) {
            return;
        }
        this.max = max;

        if (hasParent()) {
            getParent().updateMax();
        }
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(final long level) {
        if (this.level == level) {
            return;
        }
        this.level = level;

        if (hasLeft()) {
            getLeft().setLevel(level + 1);
        }
        if (hasRight()) {
            getRight().setLevel(level + 1);
        }
    }

    @SuppressWarnings("unchecked")
    public void updateMax() {
        if (isLeaf()) {
            setMax(this.end);
        } else if (isSingleParent()) {
            final Comparable singleChildMax = getSingleChild().max;
            setMax(compare(this.end, singleChildMax) < 0 ? singleChildMax : this.end);
        } else {
            final Comparable leftMax = getLeft().max;
            final Comparable rightMax = getRight().max;

            setMax(compare(leftMax, rightMax) < 0 ? rightMax : leftMax);
        }
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(final long height) {
        if (this.height == height) {
            return;
        }
        this.height = height;

        if (hasParent()) {
            getParent().updateHeight();
        }
    }

    public void updateHeight() {
        if (isLeaf()) {
            setHeight(1L);
        } else if (isSingleParent()) {
            setHeight(getSingleChild().getHeight() + 1);
        } else {
            setHeight(Math.max(getLeft().getHeight(), getRight().getHeight()) + 1);
        }
    }

    public Collection<IInterval> getIntervals() {
        return Collections.unmodifiableCollection(collection);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public boolean addInterval(final IInterval interval) {
        if (compareTo(interval) == 0) {
            return this.collection.add(interval);
        } else {
            return false;
        }
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    public boolean removeInterval(final IInterval interval) {
        return this.collection.remove(interval);
    }

    public Collection<IInterval> find(final IInterval interval) {
        return find(interval, IntervalFilters.EQUAL);
    }

    public Collection<IInterval> find(final IInterval interval, final IntervalFilter filter) {
        return this.collection.find(interval, filter);
    }

    public String getId() {
        return String.format("[%s, %s]", this.start, this.end);
    }

    @Override
    public String toString() {
        return String.format("[%s, %s] (max: %s, count: %d, level: %d, height: %d)",
                this.start, this.end, this.max, this.collection.size(), this.level, this.height);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(final IntervalTreeNode node) {
        return compareTo(node.start, node.end);
    }

    public int compareTo(final IInterval interval) {
        return compareTo(interval.getNormStart(), interval.getNormEnd());
    }

    @SuppressWarnings("unchecked")
    public int compareTo(final Comparable start, final Comparable end) {

        final int cmpStart = compare(this.start, start);
        if (cmpStart < 0) {
            return -1;
        } else if (cmpStart == 0) {
            return compare(this.end, end);
        } else {
            return 1;
        }
    }

    @SuppressWarnings("unchecked")
    public int compare(final Object val1, final Object val2) {
        return comparator.compare(val1, val2);
    }

    @Override
    public void setLeft(final IntervalTreeNode left) {
        setChild(left, IntervalTreeNodeChildType.LEFT);
    }

    @Override
    public void setRight(final IntervalTreeNode right) {
        setChild(right, IntervalTreeNodeChildType.RIGHT);
    }

    protected void setChild(final IntervalTreeNode node, final IntervalTreeNodeChildType childType) {
        if (IntervalTreeNodeChildType.LEFT.equals(childType)) {
            super.setLeft(node);
        } else if (IntervalTreeNodeChildType.RIGHT.equals(childType)) {
            super.setRight(node);
        }

        // set the new parent
        if (node != null) {
            node.setParent(this);
            node.setLevel(this.level + 1);
        }

        updateMax();
        updateHeight();
    }

    protected IntervalTreeNode get(final IntervalTreeNodeChildType childType) {
        if (IntervalTreeNodeChildType.LEFT.equals(childType)) {
            return getLeft();
        } else if (IntervalTreeNodeChildType.RIGHT.equals(childType)) {
            return getRight();
        } else {
            return null;
        }
    }

    public IntervalTreeNodeContext detach() {
        final IntervalTreeNodeContext ctx = new IntervalTreeNodeContext(this);

        // remove it from the parent
        final IntervalTreeNode parent = this.getParent();
        if (parent != null) {
            parent.removeChild(this);
        }

        this.setParent(null);
        this.setLeft(null);
        this.setRight(null);
        this.max = end;
        this.level = 0L;

        return ctx;
    }

    public void removeChild(final IntervalTreeNode node) {
        replaceChild(node, IntervalTreeNode.class.cast(null));
    }

    public void replaceChild(final IntervalTreeNode replacement, final IntervalTreeNodeChildType childType) {
        if (IntervalTreeNodeChildType.LEFT.equals(childType)) {
            setLeft(replacement);
        } else if (IntervalTreeNodeChildType.RIGHT.equals(childType)) {
            setRight(replacement);
        }
    }

    public void replaceChild(final IntervalTreeNode replacee, final IntervalTreeNode replacement) {
        if (getLeft() == replacee) {
            setLeft(replacement);
        } else if (getRight() == replacee) {
            setRight(replacement);
        }
    }

    @Override
    public Iterator<IInterval> iterator() {
        return this.collection.iterator();
    }

    public IntervalTreeNodeChildType determineChildType() {
        if (!hasParent()) {
            return IntervalTreeNodeChildType.NONE;
        } else if (getParent().getLeft() == this) {
            return IntervalTreeNodeChildType.LEFT;
        } else if (getParent().getRight() == this) {
            return IntervalTreeNodeChildType.RIGHT;
        } else {
            return IntervalTreeNodeChildType.NONE;
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (IntervalTreeNode.class.equals(obj.getClass())) {
            final IntervalTreeNode node = IntervalTreeNode.class.cast(obj);
            return compareTo(node) == 0;
        } else {
            return false;
        }
    }



    @Override
    public int hashCode() {
        return Objects.hash(getStart(), getEnd());
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(this.start);
        out.writeObject(this.end);
        out.writeObject(this.max);
        out.writeObject(this.collection);
        out.writeLong(this.level);
        out.writeLong(this.height);

        super.writeExternal(out);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.start = Comparable.class.cast(in.readObject());
        this.end = Comparable.class.cast(in.readObject());
        this.max = Comparable.class.cast(in.readObject());
        this.collection = IntervalCollection.class.cast(in.readObject());
        this.level = in.readLong();
        this.height = in.readLong();

        // we just use the first comparator in the collection, this one should work
        this.comparator = this.collection.iterator().next().getComparator();

        super.readExternal(in);
    }

    public boolean addIntervalCollectionObserver(final IntervalCollectionObserver observer) {
        if (this.collection instanceof ObservableIntervalCollection) {
            final ObservableIntervalCollection coll = ObservableIntervalCollection.class.cast(this.collection);
            coll.addObserver(observer);

            return true;
        } else {
            return false;
        }
    }
}
