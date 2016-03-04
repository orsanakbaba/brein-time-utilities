# brein-time-utilities

## When should I use the brein-time-utilities library
This library was mainly developed to make the life of developers easier, when dealing with
real-time based time-series, i.e., time-series which change based on the current time. The
library provides several data structures, to handle and manipulate such time-series.

## Quick Introduction
The current implementation of the library offers two different data structures:
* com.brein.time.timeseries.BucketTimeSeries
* com.brein.time.timeseries.ContainerBucketTimeSeries

![Example of a time-series (with buckets)](/img/example.png =300px)


## Available Data Structures

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

 So the calculation is done in three steps:
 1.) get the bucket of the new now
 2.) determine the difference between the buckets, if it's negative we are done
 3.) erase the fields in between and reset to zero or null
```
