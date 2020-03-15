package Training.AvoidMultipleAsserts;

import com.brein.time.timer.CountdownTimer;
import io.qameta.allure.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class AvoidMultipleAssertsBadExample {

    @Test
    @Owner("hozdemir") // Unit test writer name
    @Severity(SeverityLevel.CRITICAL)   // Developer should tag severity of test from one of the following : BLOCKER,CRITICAL,NORMAL,MINOR,TRIVIAL. TeamLead ot Tech.Manager may use this information to decide to step or not, if this test fail
    @Issue("KOVAN-123") // issueID on jira or any issue tracking system.
    @Story("Birden fazla assertiondan kaçınılmalıdır.")
    @Description("Test the testTimer function")
    public void testTimer() {
        final CountdownTimer timer = new CountdownTimer(1, TimeUnit.SECONDS);

        final long now = System.currentTimeMillis();
        Assert.assertTrue(timer.timerEnded(now, TimeUnit.MILLISECONDS));

        timer.startTimer();
        Assert.assertFalse(timer.timerEnded(now, TimeUnit.MILLISECONDS));
        Assert.assertFalse(timer.timerEnded(now + 500, TimeUnit.MILLISECONDS));
        Assert.assertFalse(timer.timerEnded(now + 5000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(timer.timerEnded(now + 1000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(timer.timerEnded(now + 100000, TimeUnit.MILLISECONDS));

        timer.startTimer();
        Assert.assertFalse(timer.timerEnded(now + 500, TimeUnit.MILLISECONDS));
        Assert.assertTrue(timer.timerEnded(now + 1500, TimeUnit.MILLISECONDS));
    }

}
