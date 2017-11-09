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

## Further Information

- [Processing Data with Java SE 8 Streams](http://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html)