package com.brein.time.timeintervals.indexes;

/**
 * Positions a node with the following logic:
 * <p>
 * <pre>
 * y=0         0 (x-value)
 *            / \
 *           /   \
 *          /     \
 *         /       \
 * y=1     0       1
 *        / \     / \
 *       /   \   /   \
 * y=2   0   1   2   3
 *      / \ / \ / \ / \
 * y=3  0 1 2 3 4 5 6 7
 * </pre>
 */
public class PositionedNode {
    private final IntervalTreeNode node;
    private final long x;
    private final long y;

    public PositionedNode(final IntervalTreeNode node, final long x, final long y) {
        this.node = node;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return this.node.getId();
    }

    public IntervalTreeNode getNode() {
        return this.node;
    }

    public long getX() {
        return this.x;
    }

    public long getY() {
        return this.y;
    }
    
    @Override
    public String toString() {
        return String.format("[%d, %d] - %s", this.x, this.y, this.node);
    }

    public static PositionedNode moveUp(final IntervalTreeNode parent,
                                        final PositionedNode child,
                                        final long offset) {
        long currentX = child.getX();
        for (long i = 0; i < offset; i++) {
            currentX = Double.valueOf(Math.floor(0.5 * currentX)).longValue();
        }

        return new PositionedNode(parent, currentX, child.getY() - offset);
    }

    public static PositionedNode moveLeft(final IntervalTreeNode left,
                                          final PositionedNode parent,
                                          final long offset) {
        final long x = Double.valueOf(parent.getX() * Math.pow(2, offset)).longValue();
        return new PositionedNode(left, x, parent.getY() + offset);
    }

    public static PositionedNode moveRight(final IntervalTreeNode right,
                                           final PositionedNode parent,
                                           final long offset) {
        final long x = Double.valueOf(parent.getX() * Math.pow(2, offset)).longValue() + offset;
        return new PositionedNode(right, x, parent.getY() + offset);
    }
}
