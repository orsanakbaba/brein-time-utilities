package com.brein.time.timeintervals.indexes.viewer;

import com.brein.time.timeintervals.indexes.IntervalTree;
import com.brein.time.timeintervals.indexes.IntervalTreeNode;
import com.brein.time.timeintervals.indexes.PositionedNode;
import com.brein.time.timeintervals.intervals.LongInterval;
import com.brein.time.timeintervals.intervals.TimestampInterval;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.FileSinkImages.Resolutions;
import org.graphstream.ui.view.Viewer;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicLong;

public class GraphStreamIntervalTreeViewer {
    private static final double GAP_SIZE = 1000.0;
    private static final double WIDTH_HEIGHT_RATIO = 30.0 / 10.0;

    private final IntervalTree tree;
    private Graph graph = null;

    public GraphStreamIntervalTreeViewer(final IntervalTree tree) {
        this.tree = tree;
    }

    public void save(final File file) throws IOException {

        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();

        final FileSinkImages fsi = new FileSinkImages();
        fsi.setLayoutPolicy(LayoutPolicy.NO_LAYOUT);
        fsi.setQuality(Quality.HIGH);
        fsi.setResolution(Resolutions.HD720);

        final Graph graph = getGraph();
        graph.clearSinks();
        graph.addSink(fsi);

        fsi.writeAll(graph, file.getCanonicalPath());
    }

    public void visualize() {
        final Viewer viewer = getGraph().display(false);
        viewer.disableAutoLayout();
    }

    protected Graph getGraph() {
        if (this.graph != null) {
            return this.graph;
        }

        final Graph graph = new SingleGraph("IntervalTree");

        final AtomicLong maxLevel = new AtomicLong(0);
        this.tree.positionIterator().forEachRemaining(node -> {
            final String nodeId = node.getId();
            final Node n = graph.addNode(nodeId);
            n.setAttribute("ui.label", nodeId);

            final IntervalTreeNode parent = node.getNode().getParent();
            maxLevel.set(Math.max(maxLevel.get(), node.getY()));
        });

        this.tree.positionIterator().forEachRemaining(node -> {
            final String nodeId = node.getId();
            final Node n = graph.getNode(nodeId);
            final IntervalTreeNode parent = node.getNode().getParent();

            // we have to do some modification for the coordinate system used here
            final double[] pos = calculatePos(maxLevel.get(), node);
            final double x = pos[0];
            final double y = pos[1];

            n.setAttribute("x", x);
            n.setAttribute("y", y);

            if (parent != null) {
                final String parentId = parent.getId();
                graph.addEdge(String.format("%s-%s", parentId, nodeId), parentId, nodeId);
            }
        });

        this.graph = graph;
        return graph;
    }

    protected double[] calculatePos(final long maxLevel, final PositionedNode node) {
        return new double[]{calculateXPos(maxLevel, node), calculateYPos(maxLevel, node)};
    }

    protected double calculateXPos(final long maxLevel, final PositionedNode node) {
        final long level = node.getY();

        // calculate some constant values for the binary tree with a height of maxLevel
        final double nrOfLeaves = Math.pow(2L, maxLevel);
        final double nrOfGaps = nrOfLeaves - 1;
        final double width = nrOfGaps * GAP_SIZE;

        // calculate some level specific information
        final double nrOfNodes = Math.pow(2L, level);
        final double levelGapSize = GAP_SIZE * Math.pow(2L, maxLevel - level); // the gap size doubles each level up
        final double levelOffset = 0.5 * (width - (levelGapSize * (nrOfNodes - 1)));

        return levelOffset + node.getX() * levelGapSize;
    }

    protected double calculateYPos(final long maxLevel, final PositionedNode node) {

        // calculate some constant values for the binary tree with a height of maxLevel
        final double nrOfLeaves = Math.pow(2L, maxLevel);
        final double nrOfGaps = nrOfLeaves - 1;
        final double width = nrOfGaps * GAP_SIZE;
        final double heightFactor = maxLevel == 0 ? 1.0 : width / maxLevel / WIDTH_HEIGHT_RATIO;

        return -1L * node.getY() * heightFactor;
    }

    public static void main(final String[] args) {
        final IntervalTree tree = new IntervalTree();

        //addRandomTimeStamps(tree);
        addFullTree(tree);
        //tree.remove(new NumberInterval(2L, 2L));
        //tree.remove(new NumberInterval(-1L, 1L));
        //tree.remove(new NumberInterval(0L, 1L));
        //tree.remove(new NumberInterval(2L, 2L));

        final GraphStreamIntervalTreeViewer viewer = new GraphStreamIntervalTreeViewer(tree);
        viewer.visualize();
    }

    public static void addRandomTimeStamps(final IntervalTree tree) {
        final GregorianCalendar gc = new GregorianCalendar();

        for (int i = 0; i < 10; i++) {
            final int year = randBetween(2000, 2030);
            final int dayOfYear = randBetween(1, gc.getActualMaximum(Calendar.DAY_OF_YEAR));
            gc.set(Calendar.YEAR, year);
            gc.set(Calendar.DAY_OF_YEAR, dayOfYear);

            final long unixTimestamp = Double.valueOf(Math.floor(gc.getTimeInMillis() / 1000.0)).longValue();
            final long duration = Math.round(Math.random() * 10000);

            tree.add(new TimestampInterval(unixTimestamp, unixTimestamp + duration));
        }
    }

    public static void addFullTree(final IntervalTree tree) {
        //tree.add(new NumberInterval(2L, 2L));
        tree.add(new LongInterval(0L, 1L));
        tree.add(new LongInterval(-1L, 1L));
        tree.add(new LongInterval(1L, 2L));
        tree.add(new LongInterval(4L, 4L));
        tree.add(new LongInterval(3L, 3L));
        tree.add(new LongInterval(5L, 5L));

        tree.add(new LongInterval(2L, 3L));
        tree.add(new LongInterval(2L, 4L));
        tree.add(new LongInterval(2L, 2L));

        tree.remove(new LongInterval(4L, 4L));
        tree.remove(new LongInterval(5L, 5L));
        //tree.remove(new NumberInterval(2L, 4L));
    }

    public static int randBetween(final int start, final int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }
}
