# Understanding `IntervalFilter`

In general, it is assumed that an `IntervalTree` contains the same types of intervals, i.e.,
every interval added is of the same class (i.e., `Interval` or any sub-class). This is mostly 
true, but not necessarily, especially when querying for specific intervals.

Let's assume the following initialization:

```java
final IntervalTree tree = IntervalTreeBuilder.newBuilder()
                .usePredefinedType(IntervalType.NUMBER)
                .collectIntervals(interval -> new SetIntervalCollection())
                .build();
```

The tree is capable to hold multiple **different** intervals within a node, i.e., I can easily add the
following intervals:

```java
tree.insert(new IdInterval<>("ID1", new LongInterval(1L, 5L)));
tree.insert(new IdInterval<>("ID2", new LongInterval(1L, 5L)));
```

If I know would like to validate if a specific interval can be found (or using `Collection` wording: 
"is `contained`") within the `tree`, I can do:

```java
tree.contains(new IdInterval<>("ID1", new LongInterval(1L, 5L))); // -> true
tree.contains(new IdInterval<>("ID2", new LongInterval(1L, 5L))); // -> true
tree.contains(new IdInterval<>("ID3", new LongInterval(1L, 5L))); // -> false

tree.find(new IdInterval<>("ID1", new LongInterval(1L, 5L))); // -> {[1, 5] with ID1}
tree.find(new IdInterval<>("ID2", new LongInterval(1L, 5L))); // -> {[1, 5] with ID2}
tree.find(new IdInterval<>("ID3", new LongInterval(1L, 5L))); // -> {}
```

But what if I just want to know, if the `tree` contains any interval with `[1, 5]` independent of it's id?
The answer is given by the usage of an `IntervalFilter`. In the specific example, it is not necessary to do anything
more than:

```java
tree.contains(new Interval(1L, 5L)); // -> true

tree.find(new Interval(1L, 5L));     // -> {[1, 5] with ID1, 
                                     //     [1, 5] with ID2}
```

The default used `IntervalFilter` is the so called `weakEqual` filter (see [IntervalFilters.java](../src/com/brein/time/timeintervals/filters/IntervalFilters.java)), 
which searches for intervals by utilizing the equal method of the instance, if and only if the classes of the requesting 
instance and the stored instance (in the `tree`) are equal. Otherwise, the filter will compare the `start` and `end` values 
to determine equality.

The `find` method is overloaded and provides a possibility to define your own `IntervalFilter`, see:

```java
public Collection<Interval> find(final Interval query, final IntervalFilter filter);

// Usage Examples:
tree.find(new LongInterval(1L, 5L), IntervalFilters::equal); // -> {}, empty because the example tree 
                                                             // does not contain any equal intervals

tree.find(new IdInterval<>("ID1", new LongInterval(1L, 5L)), IntervalFilters::interval); // -> {[1, 5] with ID1, 
                                                                                         //     [1, 5] with ID2}
```

You can also specify your own filters:

```java
tree.find(new Interval(1L, 5L), (i1, i2) -> false);
```

#### Note

The example can be found in the test as [TestIntervalFilter.java](../test/com/brein/time/timeintervals/docs/TestIntervalFilter.java).