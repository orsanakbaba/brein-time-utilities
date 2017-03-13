# Understanding `IntervalFilter`

In general, it is assumed that an `IntervalTree` contains the same types of intervals, i.e.,
every interval added is of the same class (i.e., `Interval` or any sub-class). This is mostly 
true, but not necessarily, especially when querying for specific intervals.

Let's assume the following initialization:

```java
final IntervalTree tree = new IntervalTree(SetIntervalCollection::new);
```

The tree is capable to hold multiple **different** intervals within a node, i.e., I can easily add the
following intervals:

```java
tree.insert(new IdInterval("ID1", 1L, 5L));
tree.insert(new IdInterval("ID2", 1L, 5L));
```

If I know would like to validate if a specific interval can be found (or using `Collection` wording: 
"is `contained`") within the `tree`, I can do:

```java
tree.contains(new IdInterval("ID1", 1L, 5L)); // -> true
tree.contains(new IdInterval("ID2", 1L, 5L)); // -> true
tree.contains(new IdInterval("ID3", 1L, 5L)); // -> false

tree.find(new IdInterval("ID1", 1L, 5L)); // -> {[1, 5] with ID1}
tree.find(new IdInterval("ID2", 1L, 5L)); // -> {[1, 5] with ID2}
tree.find(new IdInterval("ID3", 1L, 5L)); // -> {[1, 5] with ID3}
```

But what if I just want to know, if the `tree` contains any interval with `[1, 5]` independent of it's id?
The answer is given by the usage of `IntervalFilter`. In the specific example, it is not necarry to do anything
more than:

```java
tree.contains(new Interval(1L, 5L)); // -> true

tree.find(new Interval(1L, 5L));     // -> {[1, 5] with ID1, 
                                     //     [1, 5] with ID2, 
                                     //     [1, 5] with ID3}
```

The default used `IntervalFilter` is the so called `STRICT_EQUAL` filter, which searches for intervals by utilizing
the equal method of the instance, if and only if the classes of the requesting instance and the stored instance 
(in the `tree`) are equal. Otherwise, the filter will compare the `start` and `end` values to determine equality.

The `find` method is overloaded and provides a possibility to define your own `IntervalFilter`, see:

```java
public Collection<Interval> find(final Interval query, final IntervalFilter filter);

// Usage Examples:
tree.find(new Interval(1L, 5L), IntervalFilters.EQUAL); // -> {}, empty because the example tree 
                                                        // does not contain any equal intervals

tree.find(new IdInterval<>("ID1", 1L, 5L), IntervalFilters.INTERVAL); // -> {[1, 5] with ID1, 
                                                                      //     [1, 5] with ID2, 
                                                                      //     [1, 5] with ID3}
```

You can also specify your own filters:

```java
tree.find(new Interval(1L, 5L), (i1, i2) -> false);
```