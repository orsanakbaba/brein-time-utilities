package Training.DeterministicTests;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DeterministicTestsGoodExample {

    @Test
    public void concurrentAccessFromMultipleThreads() throws Exception {
        final int numberOfThreads = 10;
        final int callsPerThread = 100;
        int expectedNoOfValues = numberOfThreads * callsPerThread;

        final AtomicInteger counter = new AtomicInteger();
        final Set<Integer> values = Collections.synchronizedSet(new HashSet<Integer>());

        final CountDownLatch allThreadsComplete = new CountDownLatch(numberOfThreads);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < callsPerThread; i++) {
                    values.add(counter.getAndIncrement());
                }
                allThreadsComplete.countDown();
            }
        };

        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(runnable, "CounterThr" + i).start();
        }
        allThreadsComplete.await(10, TimeUnit.SECONDS);

        Assert.assertEquals(expectedNoOfValues, values.size());
    }
}
