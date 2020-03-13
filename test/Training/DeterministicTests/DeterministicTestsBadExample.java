package Training.DeterministicTests;

import com.brein.time.utils.TimeUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DeterministicTestsBadExample {

    //Test below shall pass ONLY when OS Region is set to a country that uses english as language.
    @Test
    public void testFormat() {
        Assert.assertEquals("Tue Nov 29 2016 11:11:30 GMT-0800 (PST)",
                TimeUtils.format("E MMM dd yyyy HH:mm:ss 'GMT'Z (z)", 1480446690L, "America/Los_Angeles"));
    }

    @Test
    public void concurrentAccessFromMultipleThreads() {
        final int numberOfThreads = 10;
        final int callsPerThread = 100;
        int expectedNoOfValues = numberOfThreads * callsPerThread;

        final AtomicInteger counter = new AtomicInteger();
        final Set<Integer> values = Collections.synchronizedSet(new HashSet<Integer>());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < callsPerThread; i++) {
                    values.add(counter.getAndIncrement());
                }
            }
        };

        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(runnable).start();
        }

        Assert.assertEquals(expectedNoOfValues, values.size());
    }
}
