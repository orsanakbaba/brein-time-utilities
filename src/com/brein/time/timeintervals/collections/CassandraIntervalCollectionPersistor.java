package com.brein.time.timeintervals.collections;

import com.brein.time.exceptions.FailedConnection;
import com.brein.time.exceptions.FailedIO;
import com.brein.time.exceptions.FailedLoad;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

public class CassandraIntervalCollectionPersistor implements IntervalCollectionPersistor, Closeable {
    public static final String KEY_COLUMN = "interval_key";
    public static final String COLL_COLUMN = "interval_collection";
    private static final Logger LOGGER = Logger.getLogger(CassandraIntervalCollectionPersistor.class);

    private final transient Lock sessionLock;
    private transient Cluster cluster;
    private transient Session session;

    private transient PreparedStatement select;
    private transient PreparedStatement upsert;
    private transient PreparedStatement delete;

    private String replicator;
    private String columnFamily;
    private String keySpace;

    public CassandraIntervalCollectionPersistor() {
        this.replicator = "{'class': 'SimpleStrategy', 'replication_factor' : 1}";
        this.columnFamily = "intervalTree";
        this.keySpace = "intervalTree";
        this.cluster = null;
        this.session = null;

        this.sessionLock = new ReentrantLock();
    }

    public void connect(final String node,
                        final int port) {
        this.sessionLock.lock();

        /*
         * 1. The session may have been closed in between. This may lead to several errors
         *    because the session may be in use by several others. Nevertheless, the
         *    implementation is assumed to be thread-safe, so let's assume that closing is
         *    too.
         * 2. The session may have never been created. In both cases, entering this block
         *    we have to create the instances.
         */
        try {

            // if we have a session meanwhile, just return
            if (this.session != null) {
                return;
            } else if (this.cluster == null) {
                this.cluster = Cluster.builder().addContactPoint(node).withPort(port).build();
                final Metadata metadata = this.cluster.getMetadata();

                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(String.format("Connected to cluster: %s", metadata.getClusterName()));

                    for (final Host host : metadata.getAllHosts()) {
                        LOGGER.info(String.format("Datacenter: %s; Host: %s; Rack: %s",
                                host.getDatacenter(), host.getAddress(), host.getRack()));
                    }
                }
            }

            this.session = this.cluster.connect();
        } catch (final Exception e) {
            final String msg = String.format("Unable to open a connection at '%s:%d (%s)'.", node, port, this.keySpace);

            /*
             * Reset the cluster, normally the current one won't work:
             * Caused by: java.lang.IllegalStateException:
             * Can't use this cluster instance because it was previously closed
             * at com.datastax.driver.core.Cluster.checkNotClosed(Cluster.java:602)
             * at com.datastax.driver.core.Cluster.connectAsync(Cluster.java:333)
             * at com.datastax.driver.core.Cluster.connectAsync(Cluster.java:309)
             * at com.datastax.driver.core.Cluster.connect(Cluster.java:251)
             * at com.brein.common.cassa.CassaSessionManager.setup(CassaSessionManager.java:94)
             */
            try {
                this.cluster.close();
            } catch (final Exception ignore) {
                // ignore
            }
            this.cluster = null;

            // now we can throw the exception
            throw new FailedConnection(msg, e);
        } finally {
            this.sessionLock.unlock();
        }

        createKeySpace();
        createColumnFamily();
    }

    protected void createKeySpace() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Checking for key-space: " + this.keySpace);
            }

            getSession().execute("USE " + this.keySpace);
        } catch (final InvalidQueryException e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Creating key-space: " + this.keySpace);
            }

            getSession().execute("CREATE KEYSPACE " + this.keySpace + " with replication = " + this.replicator);
            getSession().execute("USE " + this.keySpace);
        }
    }

    protected void createColumnFamily() {
        final String keySpace = getKeySpace();
        final String columnFamily = getColumnFamily();

        final KeyspaceMetadata keySpaceMeta = this.cluster.getMetadata().getKeyspace(keySpace);
        final TableMetadata tableMetadata = keySpaceMeta.getTable(columnFamily);

        // check if the table exists
        if (tableMetadata != null) {
            return;
        }

        final String stmt = String.format("CREATE TABLE %s (\n" +
                "  " + KEY_COLUMN + " text,\n" +
                "  " + COLL_COLUMN + " blob,\n" +
                "  PRIMARY KEY (" + KEY_COLUMN + ")\n" +
                ");", columnFamily);

        getSession().execute(stmt);
    }

    public String getKeySpace() {
        return keySpace;
    }

    public void setKeySpace(final String keySpace) {
        this.keySpace = keySpace;
    }

    public String getReplicator() {
        return replicator;
    }

    public CassandraIntervalCollectionPersistor setReplicator(final String replicator) {
        this.replicator = replicator;
        return this;
    }

    public String getColumnFamily() {
        return columnFamily;
    }

    public CassandraIntervalCollectionPersistor setColumnFamily(final String columnFamily) {
        this.columnFamily = columnFamily;
        return this;
    }

    public Session getSession() {
        if (this.session == null) {
            throw new FailedConnection("You have to establish a connection using connect(...).");
        }

        return this.session;
    }

    @Override
    public void close() {

        sessionLock.lock();
        try {

            // close the session
            if (this.session != null) {
                try {
                    this.session.close();
                } catch (final Exception e) {
                    LOGGER.error("Unable to close the session.", e);
                } finally {
                    this.session = null;
                }
            }

            // now close the cluster
            if (this.cluster != null) {
                try {
                    this.cluster.close();
                } catch (final Exception e) {
                    LOGGER.error("Unable to close the cluster.", e);
                } finally {
                    this.cluster = null;
                }
            }

            // make sure we remove all the prepared statements
            this.select = null;

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Closed database connection with keyspace '" + this.keySpace + "'.");
            }
        } finally {
            sessionLock.unlock();
        }
    }

    @Override
    public IntervalCollection load(final String key) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Loading IntervalCollection: " + key);
        }

        if (this.select == null) {
            this.select = getSession().prepare(QueryBuilder
                    .select(COLL_COLUMN)
                    .from(this.keySpace, this.columnFamily)
                    .where(eq(KEY_COLUMN, QueryBuilder.bindMarker())));
        }

        final BoundStatement boundStmt = new BoundStatement(this.select);
        boundStmt.setString(0, key);

        final ResultSet result = getSession().execute(boundStmt);
        if (result.isExhausted()) {
            return null;
        } else {
            final ByteBuffer bytes = result.one().getBytes(0);
            try (final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.array()))) {
                return IntervalCollection.class.cast(in.readObject());
            } catch (final IOException | ClassNotFoundException e) {
                throw new FailedLoad("Unable ot load instance for " + key, e);
            }
        }
    }

    @Override
    public void upsert(final IntervalCollectionEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Upserting IntervalCollection '" + event.getKey() + "': " + event.getCollection());
        }

        if (this.upsert == null) {
            this.upsert = getSession().prepare(QueryBuilder
                    .update(this.keySpace, this.columnFamily)
                    .with(QueryBuilder.set(COLL_COLUMN, QueryBuilder.bindMarker()))
                    .where(eq(KEY_COLUMN, QueryBuilder.bindMarker())));
        }

        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (final ObjectOutputStream out = new ObjectOutputStream(byteStream)) {
            out.writeObject(event.getCollection());
            out.flush();
        } catch (final IOException e) {
            throw new FailedIO("Unable ot upsert instance for " + event.getKey(), e);
        }

        final BoundStatement boundStmt = new BoundStatement(this.upsert);
        boundStmt.setBytes(0, ByteBuffer.wrap(byteStream.toByteArray()));
        boundStmt.setString(1, event.getKey());

        getSession().execute(boundStmt);
    }

    @Override
    public void remove(final IntervalCollectionEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing IntervalCollection: " + event.getKey());
        }

        if (this.delete == null) {
            this.delete = getSession().prepare(QueryBuilder.delete()
                    .from(this.keySpace, this.columnFamily)
                    .where(eq(KEY_COLUMN, QueryBuilder.bindMarker())));
        }

        final BoundStatement boundStmt = new BoundStatement(this.delete);
        boundStmt.setString(0, event.getKey());

        getSession().execute(boundStmt);
    }

    public void dropKeySpace() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Dropping key-space: " + this.keySpace);
        }
        getSession().execute("DROP KEYSPACE " + this.keySpace);
    }
}
