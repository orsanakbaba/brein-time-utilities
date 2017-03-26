package com.brein.time.timeintervals.indexes;

import com.brein.time.exceptions.FailedIO;
import com.brein.time.timeintervals.collections.IntervalCollectionFactory;
import com.brein.time.timeintervals.collections.IntervalCollectionPersistor;
import com.brein.time.timeintervals.filters.IntervalFilter;
import com.brein.time.timeintervals.filters.IntervalFilters;
import com.brein.time.timeintervals.intervals.DoubleInterval;
import com.brein.time.timeintervals.intervals.IInterval;
import com.brein.time.timeintervals.intervals.IntegerInterval;
import com.brein.time.timeintervals.intervals.LongInterval;
import com.brein.time.timeintervals.intervals.NumberInterval;
import com.brein.time.timeintervals.intervals.TimestampInterval;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IntervalTreeBuilder {

    public enum IntervalType {

        /**
         * If you plan to hold {@link NumberInterval} instances within the tree, use this type. Also use this type if
         * you plan to mix the different types of {@code NumberIntervals}, e.g., {@link IntegerInterval}, {@code
         * LongInterval}.
         */
        NUMBER(NumberInterval.class,
                IntervalFilters::weakEqual,
                IntervalValueComparator::compareNumbers,
                IntervalValueComparator::compareNumbers),
        /**
         * If you plan to hold {@link IntegerInterval} instances within the tree, use this type.
         */
        INTEGER(IntegerInterval.class,
                IntervalFilters::weakEqual,
                IntervalValueComparator::compareNumbers,
                IntervalValueComparator::compareInts),
        /**
         * If you plan to hold {@link LongInterval} instances within the tree, use this type.
         */
        LONG(LongInterval.class,
                IntervalFilters::weakEqual,
                IntervalValueComparator::compareNumbers,
                IntervalValueComparator::compareLongs),
        /**
         * If you plan to hold {@link DoubleInterval} instances within the tree, use this type.
         */
        DOUBLE(DoubleInterval.class,
                IntervalFilters::weakEqual,
                IntervalValueComparator::compareNumbers,
                IntervalValueComparator::compareDoubles),
        /**
         * If you plan to hold {@link TimestampInterval} instances within the tree, use this type.
         */
        TIMESTAMP(TimestampInterval.class,
                IntervalFilters::weakEqual,
                IntervalValueComparator::compareNumbers,
                IntervalValueComparator::compareLongs);

        private final Class<? extends IInterval> clazz;
        private final IntervalFilter intervalFilter;
        private final IntervalValueComparator comparator;
        private final IntervalValueComparator strictComparator;

        IntervalType(final Class<? extends IInterval> clazz,
                     final IntervalFilter intervalFilter,
                     final IntervalValueComparator comparator,
                     final IntervalValueComparator strictComparator) {
            this.clazz = clazz;
            this.intervalFilter = intervalFilter;
            this.comparator = comparator;
            this.strictComparator = comparator;
        }

        public IntervalFilter getIntervalFilter() {
            return intervalFilter;
        }

        public IntervalValueComparator getComparator(final boolean strict) {
            return strict ? strictComparator : comparator;
        }
    }

    private File file = null;
    private IntervalCollectionPersistor persistor = null;
    private IntervalCollectionFactory factory = null;
    private IntervalFilter filter = null;
    private IntervalValueComparator valueComparator = null;

    private boolean autoBalancing = true;
    private boolean writeCollections = false;

    public static IntervalTreeBuilder newBuilder() {
        return new IntervalTreeBuilder();
    }

    public IntervalTreeBuilder loadFromFile(final File file) {
        this.file = file;
        return this;
    }

    public IntervalTreeBuilder usePersistor(final IntervalCollectionPersistor persistor) {
        this.persistor = persistor;
        return this;
    }

    public IntervalTreeBuilder collectIntervals(final IntervalCollectionFactory factory) {
        this.factory = factory;
        return this;
    }

    public IntervalTreeBuilder overrideComparator(final IntervalValueComparator comparator) {
        this.valueComparator = comparator;
        return this;
    }

    public IntervalTreeBuilder overrideFilter(final IntervalFilter filter) {
        this.filter = filter;
        return this;
    }

    public IntervalTreeBuilder usePredefinedType(final IntervalType intervalType) {
        return usePredefinedType(intervalType, false);
    }

    public IntervalTreeBuilder usePredefinedType(final IntervalType intervalType, final boolean strict) {
        this.valueComparator = intervalType.getComparator(strict);
        this.filter = intervalType.getIntervalFilter();
        return this;
    }

    public IntervalTreeBuilder disableAutoBalancing() {
        return setAutoBalancing(false);
    }

    public IntervalTreeBuilder setAutoBalancing(final boolean autoBalancing) {
        this.autoBalancing = autoBalancing;
        return this;
    }

    public IntervalTreeBuilder enableWriteCollections() {
        return setWriteCollections(true);
    }

    public IntervalTreeBuilder setWriteCollections(final boolean writeCollections) {
        this.writeCollections = writeCollections;
        return this;
    }

    public IntervalTree build() throws FailedIO {
        if (this.file == null) {
            return buildFromSettings();
        } else if (!this.file.exists() || !this.file.isFile()) {
            return buildFromSettings();
        } else {
            final IntervalTree tree = new IntervalTree();
            final IntervalTreeConfiguration configuration = new IntervalTreeConfiguration();

            try (final FileInputStream fis = new FileInputStream(file);
                 final ObjectInput oin = new ObjectInputStream(fis)) {

                configuration.readExternal(oin);
                configuration.setPersistor(this.persistor);

                tree.setConfiguration(configuration);
                tree.readExternal(oin);
            } catch (final IOException | ClassNotFoundException e) {
                throw new FailedIO("Could not load the tree from the file: " + file, e);
            }

            return tree;
        }
    }

    protected IntervalTree buildFromSettings() {
        final IntervalTree tree = new IntervalTree();
        final IntervalTreeConfiguration configuration = new IntervalTreeConfiguration();

        configuration.setAutoBalancing(this.autoBalancing);
        configuration.setValueComparator(this.valueComparator);
        configuration.setIntervalFilter(this.filter);
        configuration.setWritingCollectionsToFile(this.writeCollections);

        configuration.setFactory(this.factory);
        configuration.setPersistor(this.persistor);

        tree.setConfiguration(configuration);

        return tree;
    }

    public static void saveToFile(final File file, final IntervalTree tree) throws FailedIO {
        try (final FileOutputStream fos = new FileOutputStream(file);
             final ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            tree.getConfiguration().writeExternal(oos);
            tree.writeExternal(oos);
        } catch (final IOException e) {
            throw new FailedIO("Could not save the tree to the file: " + file, e);
        }
    }
}
