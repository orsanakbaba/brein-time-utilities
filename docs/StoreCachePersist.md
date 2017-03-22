# Overview on Store, Cache & Persist Capabilities

The `IntervalTree` is an in-memory index-structure used to query interval-data. In general, 
the associated data to an interval can be held in-memory or in a separate data storage (e.g., 
a database).

The following illustration gives an overview over the different capabilities:

<p align="center">
  <img src="store-cache-persist.png" alt="Understanding Store, Cache & Persist" width="600">
</p>

The `IntervalTree` illustrated on the left is always kept in-memory and offers the method 
`saveToFile` (which is just an alias for the `saveToFile` method provided by [IntervalTreeBuilder.java](../src/com/brein/time/timeintervals/indexes/IntervalTree.java)).
The persisted `IntervalTree` (i.e., stored in a file on, e.g., the hard-drive) can be loaded 
whenever needed using `IntervalTree.loadFromFile(File)`.

The reference (and the actual data) between the nodes of the tree and the actual data can be stored in difference ways:
- in-memory,
- cached, and/or
- in an external data storage (e.g., database).

The following sections will walk through the different possibilities and explain pros/cons, as well 
as on how to utilize the features.

## In-Memory

When holding the associated data in-memory, the `IntervalTree` is implemented to keep strong references
to the `IntervalCollection` instances keeping the different intervals added. Thus, the collections will
only be removed, if the associated intervals (and their data) are being removed from the tree. The following
snippet creates a tree using in-memory collections:

```java
final IntervalTree tree = IntervalTreeBuilder.newBuilder()
        .usePredefinedType(IntervalType.NUMBER)
        .collectIntervals(interval -> new SetIntervalCollection())
        .build();
```

More specific, the following provided collections should be used for keeping data in-memory:

- [ListIntervalCollection](../src/com/brein/time/timeintervals/collections/ListIntervalCollection.java)
- [SetIntervalCollection](../src/com/brein/time/timeintervals/collections/SetIntervalCollection.java)

It is also possible to just create the tree and don't hold the intervals at all:

```java
final IntervalTree tree = IntervalTreeBuilder.newBuilder()
        .usePredefinedType(IntervalType.NUMBER)
        .build();
      
// this is equivalent to:
final IntervalTree tree = IntervalTreeBuilder.newBuilder()
        .usePredefinedType(IntervalType.NUMBER)
        .collectIntervals(interval -> ShallowIntervalCollection.SHALLOW_COLLECTION)
        .build();
```

The latter example may be useful, if the inserted intervals have no further need than validating 
if intervals (without attached data) are inserted (contained) or if there are overlaps with other 
intervals.

## External Data Storage

As illustrated in the first picture of this documentation, it may be that the intervals 
are associated with additional data (in the example `x`, `y`, and `z`) and should be stored 
in a separate database. The library provides a `PersistableIntervalCollectionFactory`, which
can be used exactly for this case (see [PersistableIntervalCollectionFactory.java](../src/com/brein/time/timeintervals/collections/PersistableIntervalCollectionFactory.java)). 
A `PersistableIntervalCollectionFactory` uses an `IntervalCollectionPersistor` to `load`, 
`upsert`, and `remove` intervals from an underlying source (e.g., a database). The `IntervalTree`
is designed to detected `IntervalCollectionFactory` instances, which implement the `IntervalCollectionObserver`.
If such an implementation is found, the tree automatically fires the appropriate methods for
different event:

- interval was added to a collection, fires `IntervalCollectionObserver.upsert(IntervalCollectionEvent)`
- interval was removed from a collection, fires `IntervalCollectionObserver.remove(IntervalCollectionEvent)`

These events are forwarded automatically (by the provided `PersistableIntervalCollectionFactory`) to the 
underlying `IntervalCollectionPersistor`. The library provides a default example of an `IntervalCollectionPersistor`, 
i.e., `CassandraIntervalCollectionPersistor`, which stores the whole collection as byte-stream in a Cassandra database 
(see [CassandraIntervalCollectionPersistor.java](../src/com/brein/time/timeintervals/collections/CassandraIntervalCollectionPersistor.java)).

By default, a `PersistableIntervalCollectionFactory` defines a collection to be held as `WeakReference` (`useWeakReferences()` 
returns `true`). Thus, the `IntervalTree` will query the database whenever the reference is removed by the garbage collector. 
To avoid this behavior, it is possible to change the value returned by `useWeakReferences` and keep the data in-memory, whenever
it was requested (not recommended) or utilize a cache (recommended). How to utilize the latter is explained in the following 
section.

## Utilizing a Cache

When data is used multiple times or backed by an external source, it is recommended to also utilize a cache. Utilizing a cache has several benefits:

- increase performance (i.e., looking up the same data may be faster, because cached data is used)
- control memory usage (i.e., depending on the used cache, the amount of objects held in memory or even the consumed memory can be defined)

Especially, when working with time interval data looking at the most accurate information, a cache may be of great help to 
increase performance. The sample implementation `CaffeineIntervalCollectionFactory` on how to use a cache utilizes the
[Caffeine cache](https://github.com/ben-manes/caffeine), which is a well implemented and tested cache. There are several
different ways on how to utilize a cache, the sample implementation should be of help and can be used out of the box.

A sample on how to utilize a cache with a persistor is given in the following, please make sure that you added the needed
(by default `optional`) dependencies `caffeine` and `cassandra-driver-core` in your project.

```java
// create the factory utilizing a cache
final IntervalCollectionFactory factory = 
    new CaffeineIntervalCollectionFactory(10_000, 1, TimeUnit.DAYS, interval -> new SetIntervalCollection());

// in this example we use a persistor, to store the collections in a Cassandra database
final CassandraIntervalCollectionPersistor persistor =
    new CassandraIntervalCollectionPersistor();
persistor.setKeySpace("myKeySpace");
persistor.connect("localhost", 9042);

// now we can create the tree
final IntervalTree tree = IntervalTreeBuilder.newBuilder()
        .collectIntervals(factory)
        .usePredefinedType(IntervalType.NUMBER, false)
        .usePersistor(persistor)
        .build();
```

## Persist an `IntervalTree`

Last but not least, it is important to persist the index-structure itself, so that the index will be available after the 
application, e.g., was restarted. To do that, an `IntervalTree` provides the method `saveToFile(File)`, which allows to 
persist the tree into an external file. Whenever the application is started, it is possible to recreated the tree using:

1. Example: In-memory `IntervalTree`, which persists the `IntervalCollection` instances (`IntervalTreeBuilder.enableWriteCollections()`):

    ```java
    /*
     * Create an in-memory tree, activate `enableWriteCollections`,
     * so that the collections are persisted as well.
     */
    final IntervalTree tree = IntervalTreeBuilder.newBuilder()
            .usePredefinedType(IntervalType.NUMBER)
            .collectIntervals(interval -> new ListIntervalCollection())
            .enableWriteCollections()
            .build();
    tree.saveToFile(new File("/path/to/my/persisted/intervalTree.bin"));

    /*
     * If we want to reload the tree, it is simply enough to just specify the file.
     */
    final IntervalTree reloadedTree = IntervalTreeBuilder.newBuilder()
            .loadFromFile(new File("/path/to/my/persisted/intervalTree.bin"))
            .build();
    ```
    
2. Example: Cache, persistor `IntervalTree`, which persists and loads the `IntervalCollection` utilizing the specified persistor:
    ```java
    /*
     * Create the tree utilizing a persistor and cache
     */
    final IntervalCollectionFactory factory = 
        new CaffeineIntervalCollectionFactory(10_000, 1, TimeUnit.DAYS, interval -> new SetIntervalCollection());
    
    final CassandraIntervalCollectionPersistor persistor =
        new CassandraIntervalCollectionPersistor();
    persistor.setKeySpace("myKeySpace");
    persistor.connect("localhost", 9042);
    
    final IntervalTree tree = IntervalTreeBuilder.newBuilder()
            .collectIntervals(factory)
            .usePredefinedType(IntervalType.NUMBER, false)
            .usePersistor(persistor)
            .build();
    tree.saveToFile(new File("/path/to/my/persisted/intervalTree.bin"));

    /*
     * Just reload the tree from file and specify the persistor (it's information is not 
     * peristed within the file). 
     */
    final IntervalTree reloadedTree = IntervalTreeBuilder.newBuilder()
            .loadFromFile(new File("/path/to/my/persisted/intervalTree.bin"))
            .usePersistor(persistor)
            .build();
    ```