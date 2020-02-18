package Training.DeterministicTests;

import com.brein.time.utils.TimeUtils;
import com.codahale.metrics.Counter;
import jnr.ffi.annotations.In;
import org.junit.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DeterministicTestsGoodExample {

    @Test public void concurrentAccessFromMultipleThreads() throws Exception {
        final AtomicInteger counter = new AtomicInteger();
        final int numberOfThreads = 10;
        final CountDownLatch allThreadsComplete = new CountDownLatch(numberOfThreads);
        final int callsPerThread = 100;
        final Set<Integer> values = Collections.synchronizedSet(new HashSet<Integer>());
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
            new Thread(runnable , "CounterThr" + i ).start();
        }
       allThreadsComplete.await(10, TimeUnit.SECONDS);
        //allThreadsComplete.await();
        int expectedNoOfValues = numberOfThreads * callsPerThread;
        Assert.assertEquals(expectedNoOfValues, values.size());
    }
}
