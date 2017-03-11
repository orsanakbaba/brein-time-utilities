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
    private boolean autoBalancing = true;

    public IntervalTree() {
        this(null, null);
    }

    public IntervalTree(final IntervalCollectionFactory factory) {
        this(null, factory);
    }

    public IntervalTree(final IntervalTreeNode root) {
        this(root, null);
    }

    public IntervalTree(final IntervalTreeNode root, final IntervalCollectionFactory factory) {
        this.root = root;
        this.factory = factory == null ? ListIntervalCollection::new : factory;
    }

    public boolean isAutoBalancing() {
        return this.autoBalancing;
    }

    public void setAutoBalancing(final boolean autoBalancing) {
        this.autoBalancing = autoBalancing;
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean contains(final Object o) {

        if (o instanceof Interval) {
            return !find(Interval.class.cast(o)).isEmpty();
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
        final AtomicBoolean changed = new AtomicBoolean(false);

        this.root = _add(this.root, interval, changed);
        this.size += changed.get() ? 1 : 0;

        return changed.get();
    }

    public IntervalTree insert(final Interval interval) {
        add(interval);
        return this;
    }

    protected IntervalTreeNode _add(final IntervalTreeNode node,
                                    final Interval interval,
                                    final AtomicBoolean changed) {
        if (node == null) {
            changed.set(true);
            return new IntervalTreeNode(interval, factory.get());
        }

        final IntervalTreeNodeChildType childType;
        final int cmpNode = node.compareTo(interval);
        if (cmpNode == 0) {
            changed.set(node.addInterval(interval));
            return node;
        } else if (cmpNode < 0) {
            childType = IntervalTreeNodeChildType.RIGHT;
        } else {
            childType = IntervalTreeNodeChildType.LEFT;
        }

        // add the node to the child and replace the returned value
        node.setChild(_add(node.getChild(childType), interval, changed), childType);

        // the node may have changed, thus we may have to re-balance
        return isAutoBalancing() ? balance(node) : node;
    }

    public void balance() {
        this.nodeIterator().forEachRemaining(node -> {
            if (node.isLeaf()) {
                // nothing to do
            } else if (node.isRoot()) {
                this.root = balance(node);
            } else {
                node.setLeft(balance(node.getLeft()));
                node.setRight(balance(node.getRight()));
            }
        });
    }

    public boolean isBalanced() {
        return isBalanced(this.root);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    protected boolean isBalanced(final IntervalTreeNode node) {
        if (node == null) {
            return true;
        }
        return Math.abs(determineBalance(node)) <= 1L &&
                isBalanced(node.getLeft()) && isBalanced(node.getRight());
    }

    protected IntervalTreeNode balance(final IntervalTreeNode node) {

        // check the balance
        final long balance = determineBalance(node);
        if (Math.abs(balance) <= 1) {
            return node;
        }

        // find the deepest unbalanced sub-tree (there may be more down the road)

        // validate the different four cases four unbalanced tree's
        final long balanceLeft = balance > 1L ? determineBalance(node.getLeft()) : 0L;
        final long balanceRight = balance < -1L ? determineBalance(node.getRight()) : 0L;

        // Left Left Case
        if (balance > 1 && balanceLeft >= 0) {
            return rightRotate(node);
        }
        // Right Right Case
        else if (balance < -1 && balanceRight <= 0) {
            return leftRotate(node);
        }
        // Left Right Case
        else if (balance > 1 && balanceLeft < 0) {
            node.setLeft(leftRotate(node.getLeft()));
            return rightRotate(node);
        }
        // Right Left Case
        else if (balance < -1 && balanceRight > 0) {
            node.setRight(rightRotate(node.getRight()));
            return leftRotate(node);
        }
        // any other Case, no changes - should never happen
        else {
            LOGGER.warn(String.format("Balancing node '%s' reached an unexpected state (b: %d, l: %d, r: %d)",
                    node, balance, balanceLeft, balanceRight));
            LOGGER.warn(this);
            return node;
        }
    }

    public IntervalTree delete(final Interval interval) {
        remove(interval);
        return this;
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean remove(final Object o) {
        final Interval interval;
        if (o instanceof Interval) {
            interval = Interval.class.cast(o);
        } else {
            return false;
        }

        final AtomicBoolean changed = new AtomicBoolean(false);

        this.root = _remove(this.root, interval, changed);
        this.size -= changed.get() ? 1 : 0;

        return changed.get();
    }

    protected IntervalTreeNode _remove(final IntervalTreeNode node,
                                       final Interval interval,
                                       final AtomicBoolean changed) {
        if (node == null) {
            changed.set(false);
            return null;
        }

        final IntervalTreeNodeChildType childType;
        final int cmpNode = node.compareTo(interval);
        if (cmpNode == 0) {
            if (node.removeInterval(interval)) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Removed interval '" + interval + "' from node '" + node + "'.");
                }

                changed.set(true);
                return removeEmptyNode(node);
            } else {
                changed.set(false);
                return node;
            }
        } else if (cmpNode < 0) {
            childType = IntervalTreeNodeChildType.RIGHT;
        } else {
            childType = IntervalTreeNodeChildType.LEFT;
        }

        // add the node to the child and replace the returned value
        node.setChild(_remove(node.getChild(childType), interval, changed), childType);

        return isAutoBalancing() ? balance(node) : node;
    }

    /**
     * Removes an empty node (i.e., does not contain any intervals). When removing an empty node, the resulting tree
     * must be rebalanced (if activated, see {@link #isAutoBalancing()}). The method returns the re-organized, balanced
     * sub-tree. The returned sub-tree can be at most better (in height) by one - if the tree was balanced before.
     * <p>
     * - http://www.mathcs.emory.edu/~cheung/Courses/323/Syllabus/Trees/AVL-delete.html<br/>
     * - http://quiz.geeksforgeeks.org/binary-search-tree-set-2-delete/
     *
     * @param node the node which contained the removed interval
     *
     * @return the new root to be used in replacement for the passed {@code node}
     */
    protected IntervalTreeNode removeEmptyNode(final IntervalTreeNode node) {
        if (!node.isEmpty()) {
            return node;
        }

        /*
         * There is a different between the new root (rootNode) of the sub-tree and the position where the
         * action really took place (i.e., the parent of the node, which was modified). To understand the difference,
          * it is best to look at:
         *
         * - http://www.mathcs.emory.edu/~cheung/Courses/323/Syllabus/Trees/AVL-delete.html
         */
        final IntervalTreeNode rootNode;
        final IntervalTreeNode actionNode;

        // the empty not is a leaf, just remove it
        if (node.isLeaf()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Removing leaf '" + node + "' from tree.");
            }

            rootNode = null;
            actionNode = null; // the action-node would be node.getParent(), but will be balanced in the _remove
        }
        // we have an empty node, which just have one parent, so we just keep that one parent
        else if (node.isSingleParent()) {
            rootNode = node.getSingleChild();
            actionNode = null; // the action-node would be node.getParent(), but will be balanced in the _remove

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Removing node '" + node + "' and replacing with '" +
                        rootNode + "' from tree.");
            }
        }
        // we have two sub-trees
        else {
            rootNode = findLeftLeaf(node.getRight());
            actionNode = node == rootNode.getParent() ? null : rootNode.getParent();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Removing node '" + node + "' and replacing with smallest '" +
                        rootNode + "' from tree.");
            }

            final IntervalTreeNodeChildType nodeChildType = node.determineChildType();
            final IntervalTreeNodeContext nodeCtx = node.detach();
            final IntervalTreeNodeChildType replacementChildType = rootNode.determineChildType();
            final IntervalTreeNodeContext replacementCtx = rootNode.detach();

            // if the replacementCtx has children, we have to move them to the old parent (can only have one)
            if (replacementCtx.isSingleParent()) {
                replacementCtx.getParent().setChild(replacementCtx.getSingleChild(), replacementChildType);
            }

            // it may be that the node had the smallestNode as child, in that case we have to keep the old child
            if (nodeCtx.getLeft() == rootNode) {
                rootNode.setLeft(replacementCtx.getLeft());

                rootNode.setRight(nodeCtx.getRight());
                rootNode.setParent(nodeCtx.getParent());
            } else if (nodeCtx.getRight() == rootNode) {
                rootNode.setRight(replacementCtx.getRight());

                rootNode.setLeft(nodeCtx.getLeft());
                rootNode.setParent(nodeCtx.getParent());
            } else {
                rootNode.setContext(nodeCtx);
            }
        }

        // if we replace the root, we have to let the
        if (rootNode != null && node.isRoot()) {
            rootNode.setParent(null);
            rootNode.setLevel(0L);
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("rootNode: " + rootNode);
            LOGGER.trace("actionNode: " + actionNode);
        }

        if (!isAutoBalancing()) {
            return rootNode;
        } else if (rootNode == null) {
            return null;
        } else if (actionNode == null) {
            return balance(rootNode);
        } else {

            /*
             * In this case, we have to follow up from the actionNode up to the rootNode
             * and make sure everything is balanced within this area.
             */
            IntervalTreeNodeChildType childType = actionNode.determineChildType();
            IntervalTreeNode n = actionNode.getParent();

            do {
                final IntervalTreeNode child = n.getChild(childType);
                n.setChild(balance(child), childType);

                if (n == rootNode) {
                    break;
                }

                childType = n.determineChildType();
                n = n.getParent();
            } while (true);

            return balance(rootNode);
        }
    }

    // Get Balance factor of node N
    protected long determineBalance(final IntervalTreeNode node) {
        if (node == null) {
            return 0L;
        }

        return (node.hasLeft() ? node.getLeft().getHeight() : 0) -
                (node.hasRight() ? node.getRight().getHeight() : 0);
    }

    protected IntervalTreeNode leftRotate(final IntervalTreeNode node) {
        final IntervalTreeNode right = node.getRight();

        final IntervalTreeNodeContext rightCtx = right.detach();
        final IntervalTreeNodeContext nodeCtx = node.detach();

        node.setLeft(nodeCtx.getLeft());
        node.setRight(rightCtx.getLeft());
        right.setLeft(node);
        right.setRight(rightCtx.getRight());

        return right;
    }

    protected IntervalTreeNode rightRotate(final IntervalTreeNode node) {
        final IntervalTreeNode left = node.getLeft();

        final IntervalTreeNodeContext leftCtx = left.detach();
        final IntervalTreeNodeContext nodeCtx = node.detach();

        node.setLeft(leftCtx.getRight());
        node.setRight(nodeCtx.getRight());
        left.setRight(node);
        left.setLeft(leftCtx.getLeft());

        return left;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        toString("", this.root, sb, true);
        return sb.toString();
    }

    private void toString(final String prefix,
                          final IntervalTreeNode node,
                          final StringBuilder sb,
                          final boolean tail) {

        sb.append(prefix)
                .append(tail ? "└── " : "├── ")
                .append(node)
                .append(System.lineSeparator());

        if (node == null || node.isLeaf()) {
            return;
        }

        final String newPrefix = prefix + (tail ? "    " : "│   ");
        toString(newPrefix, node.getLeft(), sb, false);
        toString(newPrefix, node.getRight(), sb, true);
    }
}
