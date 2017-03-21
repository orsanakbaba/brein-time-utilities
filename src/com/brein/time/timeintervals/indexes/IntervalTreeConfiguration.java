package com.brein.time.timeintervals.indexes;

import com.brein.time.exceptions.IllegalConfiguration;
import com.brein.time.timeintervals.collections.IntervalCollectionFactory;
import com.brein.time.timeintervals.collections.IntervalCollectionPersistor;
import com.brein.time.timeintervals.filters.IntervalFilter;
import org.apache.log4j.Logger;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

@SuppressWarnings("NullableProblems")
public class IntervalTreeConfiguration implements Externalizable {
    private static final Logger LOGGER = Logger.getLogger(IntervalTreeConfiguration.class);

    private boolean autoBalancing = true;
    private boolean usesPersistor = false;
    private boolean writingCollectionsToFile = false;
    private IntervalValueComparator valueComparator = null;
    private IntervalFilter intervalFilter = null;
    private IntervalCollectionFactory factory = null;

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeBoolean(this.autoBalancing);
        out.writeBoolean(this.usesPersistor);
        out.writeBoolean(this.writingCollectionsToFile);
        out.writeObject(this.valueComparator);
        out.writeObject(this.intervalFilter);
        out.writeObject(this.factory);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.autoBalancing = in.readBoolean();
        this.usesPersistor = in.readBoolean();
        this.writingCollectionsToFile = in.readBoolean();
        this.valueComparator = IntervalValueComparator.class.cast(in.readObject());
        this.intervalFilter = IntervalFilter.class.cast(in.readObject());
        this.factory = IntervalCollectionFactory.class.cast(in.readObject());
    }

    public void setAutoBalancing(final boolean autoBalancing) {
        this.autoBalancing = autoBalancing;
    }

    public boolean isAutoBalancing() {
        return autoBalancing;
    }

    public void setValueComparator(final IntervalValueComparator valueComparator) {
        this.valueComparator = valueComparator;
    }

    public IntervalValueComparator getValueComparator() {
        return valueComparator;
    }

    public void setFactory(final IntervalCollectionFactory factory) {
        this.factory = factory;
    }

    public IntervalCollectionFactory getFactory() {
        return this.factory;
    }

    public void setPersistor(final IntervalCollectionPersistor persistor) throws IllegalConfiguration {
        if (this.factory != null) {
            this.factory.usePersistor(persistor);
            this.usesPersistor = persistor != null;
        } else {
            this.usesPersistor = false;
        }
    }

    public boolean isUsingPersistor() {
        return usesPersistor;
    }

    public boolean isWritingCollectionsToFile() {
        return writingCollectionsToFile;
    }

    public void setWritingCollectionsToFile(final boolean writingCollectionsToFile) {
        this.writingCollectionsToFile = writingCollectionsToFile;
    }

    public IntervalFilter getIntervalFilter() {
        return intervalFilter;
    }

    public void setIntervalFilter(final IntervalFilter intervalFilter) {
        this.intervalFilter = intervalFilter;
    }
}
