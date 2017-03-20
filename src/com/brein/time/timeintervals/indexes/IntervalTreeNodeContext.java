package com.brein.time.timeintervals.indexes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class IntervalTreeNodeContext implements Externalizable {
    private transient IntervalTreeNode parent;

    // the left and right are
    protected IntervalTreeNode left;
    protected IntervalTreeNode right;

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

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(this.left);
        out.writeObject(this.right);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {

        // the parent is set based on the outer resolver, we don't want to load a new instance of a parent
        setLeft(IntervalTreeNode.class.cast(in.readObject()));
        setRight(IntervalTreeNode.class.cast(in.readObject()));
    }
}
