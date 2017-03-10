package com.brein.time.timeintervals.indexes;

import com.brein.time.timeintervals.collections.IntervalCollection.IntervalFilter;
import com.brein.time.timeintervals.collections.IntervalCollection.IntervalFilters;
import com.brein.time.timeintervals.collections.IntervalCollectionFactory;
import com.brein.time.timeintervals.collections.ListIntervalCollection;
import com.brein.time.timeintervals.intervals.Interval;
import org.apache.log4j.Logger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings("NullableProblems")
public class IntervalTree implements Collection<Interval> {
    private static final Logger LOGGER = Logger.getLogger(IntervalTree.class);

    private final IntervalCollectionFactory factory;

    private IntervalTreeNode root = null;
    private long size = 0L;

    public IntervalTree() {
        this(ListIntervalCollection::new);
    }

    public IntervalTree(final IntervalCollectionFactory factory) {
        this.factory = factory;
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean contains(final Object o) {

        if (o instanceof Interval) {
            return find(Interval.class.cast(o)).isEmpty();
        } else {
            return false;
        }
    }

    public Collection<Interval> find(final Interval query) {
        return find(query, IntervalFilters.STRICT_EQUAL);
    }

    public Collection<Interval> find(final Interval query, final IntervalFilter filter) {
        if (this.root == null) {
            return Collections.emptyList();
        } else {
            return _find(this.root, query, filter);
        }
    }

    protected Collection<Interval> _find(final IntervalTreeNode node,
                                         final Interval query,
                                         final IntervalFilter filter) {
        if (node == null) {
            return Collections.emptyList();
        }

        // check if the current node overlaps
        final int cmpNode = node.compareTo(query);
        if (cmpNode == 0) {
            return node.find(query, filter);
        } else if (cmpNode < 0) {
            return _find(node.getRight(), query, filter);
        } else {
            return _find(node.getLeft(), query, filter);
        }
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        for (final Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(final Collection<? extends Interval> c) {
        final AtomicBoolean changed = new AtomicBoolean(false);
        c.forEach(interval -> changed.compareAndSet(false, add(interval)));
        return changed.get();
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        final AtomicBoolean changed = new AtomicBoolean(false);
        c.forEach(interval -> changed.compareAndSet(false, remove(interval)));
        return changed.get();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        final List<Interval> contained = c.stream()
                .filter(this::contains)
                .map(Interval.class::cast)
                .collect(Collectors.toList());

        // if there is nothing to be removed, we can return false
        if (contained.size() == this.size()) {
            return false;
        }

        clear();
        addAll(contained);

        return true;
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    public Collection<Interval> overlap(final Interval query) {
        if (this.root == null) {
            return Collections.emptyList();
        } else {
            final List<Interval> result = new ArrayList<>();
            _overlap(this.root, query, result);
            return result;
        }
    }

    protected void _overlap(final IntervalTreeNode node, final Interval query, final Collection<Interval> result) {

        if (node == null) {
            return;
        }

        // check if the current node overlaps
        if (node.getStart() <= query.getNormEnd() && node.getEnd() >= query.getNormStart()) {
            result.addAll(node.getIntervals());
        }

        if (node.hasLeft() && node.getLeft().getMax() >= query.getStart()) {
            this._overlap(node.getLeft(), query, result);
        }

        this._overlap(node.getRight(), query, result);
    }

    @Override
    public boolean add(final Interval interval) {
        final boolean result;

        if (this.root == null) {
            this.root = new IntervalTreeNode(interval, factory.get());
            result = true;
        } else {
            result = _add(this.root, interval) != null;
        }

        this.size += result ? 1 : 0;

        return result;
    }

    public IntervalTree insert(final Interval interval) {
        add(interval);
        return this;
    }

    protected IntervalTreeNode _add(final IntervalTreeNode node, final Interval interval) {
        if (node == null) {
            return new IntervalTreeNode(interval, factory.get());
        }

        final int cmpNode = node.compareTo(interval);
        if (cmpNode == 0) {
            if (!node.addInterval(interval)) {
                return null;
            }
        } else if (cmpNode < 0) {
            node.setRight(_add(node.getRight(), interval));
        } else {
            node.setLeft(_add(node.getLeft(), interval));
        }

        return node;
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean remove(final Object o) {
        final boolean result;

        if (this.root == null) {
            result = false;
        } else if (o instanceof Interval) {
            result = _remove(this.root, Interval.class.cast(o));
        } else {
            result = false;
        }

        size -= result ? 1 : 0;

        return result;
    }

    public IntervalTree delete(final Interval interval) {
        remove(interval);
        return this;
    }

    protected boolean _remove(final IntervalTreeNode node, final Interval interval) {
        if (node == null) {
            return false;
        }

        final int cmpNode = node.compareTo(interval);
        if (cmpNode == 0) {
            final boolean result = node.removeInterval(interval);

            if (node.isEmpty()) {

                // we have to remove the node and see if we have someone as replacement
                if (node.isLeaf()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Removing leaf '" + node + "' from tree.");
                    }

                    if (node.hasParent()) {
                        node.getParent().removeChild(node);
                    } else {
                        this.root = null;
                    }
                } else if (node.isSingleParent()) {
                    final IntervalTreeNode replacementNode = node.getSingleChild();

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Removing node '" + node + "' and replacing with '" +
                                replacementNode + "' from tree.");
                    }

                    if (node.hasParent()) {
                        node.getParent().replaceChild(node, replacementNode);
                    } else {
                        replacementNode.setParent(null);
                        this.root = replacementNode;
                    }
                } else {

                    // determine which node to replace the deleted one with (right sub-tree, smallest value)
                    final IntervalTreeNode smallestNode = findLeftLeaf(node.getRight());
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Removing node '" + node + "' and replacing with smallest '" +
                                smallestNode + "' from tree.");
                    }

                    final IntervalTreeNode replacementNode = replaceNode(node, smallestNode);
                    if (!node.hasParent()) {
                        this.root = replacementNode;
                    }
                }
            } else {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Removed interval '" + interval + "' from node '" + node + "'.");
                }
            }

            return result;
        } else if (cmpNode < 0) {
            return _remove(node.getRight(), interval);
        } else {
            return _remove(node.getLeft(), interval);
        }
    }

    protected IntervalTreeNode replaceNode(final IntervalTreeNode replacee, final IntervalTreeNode replacement) {
        final IntervalTreeNodeChildType childType = replacee.determineChildType();
        final IntervalTreeNodeContext replaceeCtx = replacee.detach();
        final IntervalTreeNodeContext replacementCtx = replacement.detach();

        // it may be that the replacee had the replacement as child, in that case we have to keep the old child
        if (replaceeCtx.getLeft() == replacement) {
            replacement.setLeft(replacementCtx.getLeft());

            replacement.setRight(replaceeCtx.getRight());
            replacement.setParent(replaceeCtx.getParent());
        } else if (replaceeCtx.getRight() == replacement) {
            replacement.setRight(replacementCtx.getRight());

            replacement.setLeft(replaceeCtx.getLeft());
            replacement.setParent(replaceeCtx.getParent());
        } else {
            replacement.setContext(replaceeCtx);
        }

        if (replaceeCtx.hasParent()) {
            replaceeCtx.getParent().replaceChild(replacement, childType);
        }

        return replacement;
    }

    protected IntervalTreeNode findLeftLeaf(final IntervalTreeNode startNode) {
        if (startNode == null) {
            return null;
        }

        IntervalTreeNode node = startNode;
        while (node.getLeft() != null) {
            node = node.getLeft();
        }

        return node;
    }

    protected PositionedNode findLeftLeaf(final PositionedNode posNode) {
        if (posNode == null || posNode.getNode() == null) {
            return null;
        }

        IntervalTreeNode node = posNode.getNode();
        long offset = 0;
        while (node.getLeft() != null) {
            node = node.getLeft();
            offset++;
        }

        return PositionedNode.moveLeft(node, posNode, offset);
    }

    @Override
    public int size() {
        return size < Integer.MAX_VALUE ? Long.valueOf(size).intValue() : Integer.MAX_VALUE;
    }

    @Override
    public boolean isEmpty() {
        return this.root == null;
    }

    @Override
    public Iterator<Interval> iterator() {
        final Iterator<IntervalTreeNode> outerNodeIt = nodeIterator();

        return new Iterator<Interval>() {
            private Interval next = findNext();
            private Iterator<Interval> nodeCollectionIt = null;

            @Override
            public boolean hasNext() {
                return this.next != null;
            }

            @Override
            public Interval next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                final Interval result = this.next;
                this.next = findNext();

                return result;
            }

            protected Interval findNext() {
                if (this.nodeCollectionIt != null && this.nodeCollectionIt.hasNext()) {
                    // nothing to do, next will return something
                } else if (outerNodeIt.hasNext()) {
                    this.nodeCollectionIt = outerNodeIt.next().iterator();
                } else {
                    return null;
                }

                return this.nodeCollectionIt.next();
            }
        };
    }

    public Iterator<IntervalTreeNode> nodeIterator() {
        final IntervalTreeNode outerFirstNext = findLeftLeaf(this.root);

        return new Iterator<IntervalTreeNode>() {
            private IntervalTreeNode next = outerFirstNext;

            @Override
            public boolean hasNext() {
                return this.next != null;
            }

            @Override
            public IntervalTreeNode next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                final IntervalTreeNode result = this.next;
                if (this.next.getRight() != null) {
                    this.next = findLeftLeaf(this.next.getRight());
                    return result;
                } else {
                    while (true) {
                        final IntervalTreeNode parent = this.next.getParent();
                        if (parent == null) {
                            this.next = null;
                            return result;
                        } else if (parent.getLeft() == this.next) {
                            this.next = parent;
                            return result;
                        } else {
                            this.next = parent;
                        }
                    }
                }
            }
        };
    }

    public Iterator<PositionedNode> positionIterator() {
        final PositionedNode outerFirstNext = findLeftLeaf(new PositionedNode(this.root, 0L, 0L));

        return new Iterator<PositionedNode>() {
            private PositionedNode next = outerFirstNext;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public PositionedNode next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                final PositionedNode result = this.next;
                final IntervalTreeNode right = this.next.getNode().getRight();

                if (right == null) {
                    while (true) {
                        final IntervalTreeNode node = this.next.getNode();
                        final IntervalTreeNode parent = node.getParent();

                        if (parent == null) {
                            this.next = null;
                            return result;
                        }

                        final PositionedNode posParent = PositionedNode.moveUp(parent, this.next, 1L);
                        if (parent.getLeft() == node) {
                            this.next = posParent;
                            return result;
                        } else {
                            this.next = posParent;
                        }
                    }
                } else {
                    final PositionedNode posRight = PositionedNode.moveRight(right, this.next, 1L);
                    this.next = findLeftLeaf(posRight);
                    return result;
                }
            }
        };
    }

    @Override
    public Object[] toArray() {
        if (this.size() == 0L) {
            return new Interval[0];
        }

        final Interval[] intervals = new Interval[size()];
        final AtomicInteger pos = new AtomicInteger(0);
        iterator().forEachRemaining(i -> intervals[pos.getAndIncrement()] = i);

        return intervals;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(final T[] arr) {

        final T[] intervals;
        if (arr.length < this.size) {
            intervals = (T[]) Array.newInstance(arr.getClass().getComponentType(), size());
        } else {
            intervals = arr;
        }

        final AtomicInteger pos = new AtomicInteger(0);
        iterator().forEachRemaining(i -> intervals[pos.getAndIncrement()] = (T) i);

        for (int i = intervals.length; i < size(); i++) {
            intervals[i] = null;
        }

        return intervals;
    }
}
