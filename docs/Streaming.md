# Streaming the `IntervalTree`

When iterating over a Java `Collection`, it is pretty common to do so
by using an `Iterator`. There are many discussion on how to iterate over
an amount of items, e.g.,
[1](https://stackoverflow.com/questions/31210791/iterator-versus-stream-of-java-8),
[2](http://blog.takipi.com/benchmark-how-java-8-lambdas-and-streams-can-make-your-code-5-times-slower/).

Besides the nicely readable code, `Streams` may have positive or negative
impact on the performance of someones application. Until version `1.6.3`
the `IntervalTree` provided "only" a `stream()` and `iterator()`, as needed by
the `Collection` interface, whereby the `stream()` implementation was
based on the `iterator()`. Both of these methods enabled the user to iterate
over the full content of the `IntervalTree`.

Since `1.6.3` the `IntervalTree` also provides a `overlapStream()` method,
which "pushes" the overlapping intervals through the stream.

## Examples:

```
    public void example() {
        final IntervalTree tree = IntervalTreeBuilder.newBuilder()
                .usePredefinedType(IntervalType.NUMBER, false)
                .collectIntervals(interval -> new ListIntervalCollection())
                .enableWriteCollections()
                .build();

        // let's add 1_000_000 intervals from 0, 1_000_000 -> 99, 1_000_000 and a random id
        IntStream.range(0, 1_000_000).forEach(i ->
                tree.insert(new IdInterval<>(UUID.randomUUID(), i, 1_000_000)));

        // Now let's find 3 intervals, which:
        //   1. overlap with [1, 10_000] (which are all), and
        //   2. have an `id` (or whatever attribute) that starts with "1"
        // The streaming will ensure stop any further calculation, when 3 intervals are found
        final List<IdInterval<UUID, Integer>> res = tree.overlapStream(new IntegerInterval(1, 10_000))
                .map(interval -> (IdInterval<UUID, Integer>) interval)
                .filter(interval -> interval.getId().toString().startsWith("1"))
                .limit(3)
                .collect(Collectors.toList());

        // output
        System.out.println(res);
    }
```

## Further Information

- [Processing Data with Java SE 8 Streams](http://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html)