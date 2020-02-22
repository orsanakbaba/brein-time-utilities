package Training.DeterministicTests;

import com.brein.time.utils.TimeUtils;
import com.codahale.metrics.Counter;
import jnr.ffi.annotations.In;
import org.junit.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DeterministicTestsBadExample {

    //Test below shall pass ONLY when OS Region is set to a country that uses english as language.
    @Test
    public void testFormat() {

    }

    @Test
    public void concurrentAccessFromMultipleThreads() throws Exception {
        final AtomicInteger counter = new AtomicInteger();
        final int callsPerThread = 100;
        final Set<Integer> values = Collections.synchronizedSet(new HashSet<Integer>());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < callsPerThread; i++) {
                    values.add(counter.getAndIncrement());
                }
            }
        };

        int threads = 10;
        for (int i = 0; i < threads; i++) {
            new Thread(runnable).start();
        }
        int expectedNoOfValues = threads * callsPerThread;
        Assert.assertEquals(expectedNoOfValues, values.size());
    }
}
