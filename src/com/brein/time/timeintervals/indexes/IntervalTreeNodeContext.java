package com.brein.time.timeintervals.indexes;

public class IntervalTreeNodeContext {
    private IntervalTreeNode parent;
    private IntervalTreeNode left;
    private IntervalTreeNode right;

    public IntervalTreeNodeContext() {
        // nothing
    }

    public IntervalTreeNodeContext(final IntervalTreeNode parent,
                                   final IntervalTreeNode left,
                                   final IntervalTreeNode right) {
        this();

        this.parent = parent;
        this.left = left;
        this.right = right;
    }

    public IntervalTreeNodeContext(final IntervalTreeNode node) {
        this(node.getParent(), node.getLeft(), node.getRight());
    }

    public IntervalTreeNode getParent() {
        return parent;
    }

    public void setParent(final IntervalTreeNode parent) {
        this.parent = parent;
    }

    public IntervalTreeNode getChild(final IntervalTreeNodeChildType childType) {
        if (childType.equals(IntervalTreeNodeChildType.LEFT)) {
            return getLeft();
        } else if (childType.equals(IntervalTreeNodeChildType.RIGHT)) {
            return getRight();
        } else {
            return null;
        }
    }

    public IntervalTreeNode getLeft() {
        return left;
    }

    public void setLeft(final IntervalTreeNode left) {
        this.left = left;
    }

    public IntervalTreeNode getRight() {
        return right;
    }

    public void setRight(final IntervalTreeNode right) {
        this.right = right;
    }

    public boolean hasLeft() {
        return this.left != null;
    }

    public boolean hasRight() {
        return this.right != null;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public boolean isChild(final IntervalTreeNode node) {
        return this.left == node || this.right == node;
    }

    public boolean isLeaf() {
        return !hasLeft() && !hasRight();
    }

    public boolean isSingleParent() {
        return hasLeft() ^ hasRight();
    }

    public boolean isFullParent() {
        return hasLeft() && hasRight();
    }

    public IntervalTreeNode getSingleChild() {
        if (!isSingleParent()) {
            throw new IllegalStateException("Cannot retrieve single child from not single parent, " +
                    "validate with 'isSingleParent'");
        }

        return hasLeft() ? getLeft() : getRight();
    }

    public void setContext(final IntervalTreeNodeContext ctx) {
        setParent(ctx.getParent());
        setLeft(ctx.getLeft());
        setRight(ctx.getRight());
    }

    public boolean isInContext(final IntervalTreeNode node) {
        return isChild(node) || this.parent == node;
    }

    public boolean isRoot() {
        return !hasParent();
    }

    @Override
    public String toString() {
        return String.format("P: %s, L: %s, R: %s", this.parent, this.left, this.right);
    }
}
