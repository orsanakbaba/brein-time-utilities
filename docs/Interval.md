# What is an `Interval`

An interval defines a sub-set containing all the values lying between two values `s` and `e`
of a domain. Formally, an interval `[s, e]` of the domain X is defined as `{ x ∈ X | s ≤ x ∧ x ≤ e }`.
An interval can be open (on both or only one end), i.e.:
- `(s, e] := { x ∈ X | s < x ∧ x ≤ e }`.
- `[s, e) := { x ∈ X | s ≤ x ∧ x < e }`.
- `(s, e) := { x ∈ X | s < x ∧ x < e }`.
 
## Usage Examples

Within this library an interval is implemented by the `Interval` class, which internally implements the `IInterval` 
interface. The latter is used by the [`IntervalTree`](../README.md) implementation, also available within this library.

The provided implementation of the `IInterval` is able to handle all types of primitive numbers (using the
object-oriented representative, i.e., `Byte`, `Short`, `Integer`, `Long`, `Float`, `Double`).

```java
final Interval<Integer> sample1 = new Interval<>(5, 10);    // interval: [5, 10]
final Interval<Long> sample2 = new Interval<>(-123L, 123L); // interval: [-123, 123]
final Interval<Double> sample3 = new Interval<>(1.1, 2.2);  // interval: [1.1, 2.2]

// you can also specify the type of values stored
final Interval<Integer> i = new Interval<>(1, 5);  // interval: [1, 5]

// there are no simplified constructors defined, if you want to use open intervals
final Interval<Integer> openStart = new Interval<>(Integer.class, 1, 5, true, false);  // interval: (1, 5]
final Interval<Integer> openEnd = new Interval<>(Integer.class, 1, 5, false, true);    // interval: [1, 5)
final Interval<Integer> bothOpen = new Interval<>(Integer.class, 1, 5, true, true);    // interval: (1, 5)

// it is not allowed to specify 'invalid' intervals, i.e., end > start
final Interval<Integer> invalid1 = new Interval<>(5, 4);                               // invalid: [5, 4]
final Interval<Integer> invalid2 = new Interval<>(Integer.class, 5, 5, true, false);   // invalid: (5, 5]
```

## Operations

The implementation provides the following operations:
- `boolean contains(Object value)`
- `int compareTo(final IInterval interval)`
- `boolean equals(final Object obj)`

The `contains` method, checks if the interval contains the specified `value`, i.e., it returns true, if and only if 
`value ≤ x ∧ value ≤ e` assuming a closed interval `[s, e]`. The `value` can be of any valid type within the domain, 
(the `Interval` implementation assumes all (primitive) `Number` to be in the same domain), e.g.:

```java
new Interval<>(1L, 10L).contains(5) == true;

new Interval<>(1L, 10L).contains(1.0) == true;
new Interval<>(1L, 10L).contains(10.1) == false;
new Interval<>(1L, 10L).contains(10.0) == true

new Interval<>(Long.class, 1L, 2L, false, true).contains(2) == false;
```


The `compareTo` method validates, if an interval is smaller 
(`< 0`), equal (`== 0`), or larger (`> 0`) than another interval. The method allows to use a different types interval to
compare with, e.g., 

```java
new Interval<>(1L, 5L).compareTo(new Interval<>(1L, 5L))   == 0;  // i.e., equal
new Interval<>(1L, 5L).compareTo(new Interval<>(1.0, 5.0)) == 0;  // i.e., equal
new Interval<>(1L, 5L).compareTo(new Interval<>(0.9, 1.0))  > 0;  // i.e., [1, 5] > [0.9, 1.0]
new Interval<>(1L, 5L).compareTo(new Interval<>(1, 6))      < 0;  // i.e., [1, 5] < [1, 6]

// the specified type, specifies the boundaries
new Interval<>(Double.class, 1.0, 5.0, true, true).compareTo(new Interval<>(Long.class, 1L, 5L, true, true)) > 0;  // i.e., (1.0, 5.0) < (1, 5)
```