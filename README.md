# brein-time-utilities

## When should I use the brein-time-utilities library
This library was mainly developed to make the life of developers easier, when dealing with
real-time based time-series, i.e., time-series which change based on the current time. The
library provides several data structures, to handle and manipulate such time-series.

## Quick Introduction
The library provides index and data structures used to handle real-time associated data
(e.g., unixTimestamp, value). In general the operation are fast (e.g., set and changing
 now is done in O(1)). All implemented data structures support:
* buckets (i.e., define a range of time-points the data is associated to)
* moving of "now" without modifying the whole array of the time-series (utilizing rolling)

## Available Index Structures
The current implementation of the library offers the following index structures:
- `com.brein.time.timeintervals.indexes.IntervalTree` (since v1.5.0)

### IntervalTree
The `IntervalTree` is an often used index-structure to find intervals within a data-set. There 
are several implementations available Java (e.g., [[1]](https://github.com/kevinjdolan/intervaltree)
[[2]](https://github.com/search?l=Java&p=1&q=intervaltree&type=Repositories&utf8=%E2%9C%93), and for 
other languages [[3]](https://github.com/chaimleib/intervaltree)[[4]](http://code.google.com/p/intervaltree/), 
as well as the [relational interval tree](http://blogs.solidq.com/en/sqlserver/static-relational-interval-tree/), 
which can be used within relational database management systems. The presented implementation 
is not only well tested and handles multiple million intervals a day, it can also be persisted 
and can use a database management system to retrieve the different intervals data from an established
database system, as well as utilize caching techniques.

Further information regarding the actual information can be found here [[5]](http://www.geeksforgeeks.org/interval-tree/)
and [[6]](http://www.davismol.net/2016/02/07/data-structures-augmented-interval-tree-to-search-for-interval-overlapping/).

In the following you can find a usage example, nevertheless for more advanced examples it is recommended
to have a look at the tests, maintained in this repository.

```java

```

## Available Data Structures
The current implementation of the library offers the following data structures:
- `com.brein.time.timeseries.BucketTimeSeries` (since v1.0.0)
- `com.brein.time.timeseries.ContainerBucketTimeSeries` (since v1.0.0)

### BucketTimeSeries
The BucketTimeSeries is used to group time-points into buckets and keep a time-series
for these buckets. As all of the time-series of this library, this time-series is also
a rolling time-series regarding the now time point, i.e., it contains information about
now and n time-buckets into the pass. The following illustration explains the structure:

```
 The data structure (which is based on an array) can be explained best with
 an illustration (with n == timeSeriesSize):

   [0] [1] [2] [3] [4] [5] [6] ... [n]
        ↑
  currentNowIdx

 Each array field is a bucket of time-stamps (ordered back from now):

   [1] ==> [1456980000, 1456980300) ← now, 1 == currentNowIdx
   [2] ==> [1456970700, 1456980000)
   ...
   [n] ==> ...
   [0] ==> ...
```

Whenever the now time point "moves" forward using:

```java
public void setNow(final long unixTimeStamp) throws IllegalTimePointMovement
```

the data structure is updated using:

```
 Getting the new currentNowIdx is done by calculating the
 difference between the old now and the new now and moving
 the currentNowIdx forward.

  [0] [1] [2] [3] [4] [5] [6]
       ↑
 currentNowIdx

 Assume we move the now time stamp forward by three buckets:

  [0] [1] [2] [3] [4] [5] [6]
                       ↑
                 currentNowIdx

 So the calculation is done in two steps:
 1.) get the bucket of the new now
 2.) determine the difference between the buckets, if it's negative => error,
     if it is zero => done, otherwise => erase the fields in between and reset
     to zero or null
```
