package com.brein.time.timeintervals.indexes;

import com.brein.time.timeintervals.intervals.Interval;
import org.junit.Test;

public class TestPerformanceIntervalTree {

    @Test
    public void testMemory() {

        for (int i = 0; i < 10; i++) {
            System.gc();

            final long m1 = Runtime.getRuntime().freeMemory();

            final IntervalTree tree = new IntervalTree();
            final long m2 = Runtime.getRuntime().freeMemory();

            tree.insert(new Interval(1L, 2L));
            final long m3 = Runtime.getRuntime().freeMemory();

            tree.insert(new Interval(1L, 2L));
            final long m4 = Runtime.getRuntime().freeMemory();

            tree.insert(new Interval(2L, 3L));
            final long m5 = Runtime.getRuntime().freeMemory();

            tree.insert(new Interval(2L, 3L));
            tree.insert(new Interval(2L, 3L));
            tree.insert(new Interval(2L, 3L));
            tree.insert(new Interval(2L, 3L));
            tree.insert(new Interval(2L, 3L));
            final long m6 = Runtime.getRuntime().freeMemory();

            System.out.println("tree     : " + (m1 - m2) + " bytes");
            System.out.println("node     : " + (m2 - m3) + " bytes");
            System.out.println("interval : " + (m3 - m4) + " bytes");
            System.out.println("node     : " + (m4 - m5) + " bytes");
            System.out.println("intervals: " + (m5 - m6) + " bytes");
        }

    }
}
