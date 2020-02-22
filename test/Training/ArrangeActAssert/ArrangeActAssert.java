package Training.ArrangeActAssert;

import com.brein.time.utils.TimeUtils;
import io.qameta.allure.*;
import org.junit.Assert;
import org.junit.Test;

public class ArrangeActAssert {

    @Test
    @Owner("hozdemir") // Unit test writer name
    @Severity(SeverityLevel.CRITICAL)   // Developer should tag severity of test from one of the following : BLOCKER,CRITICAL,NORMAL,MINOR,TRIVIAL. TeamLead ot Tech.Manager may use this information to decide to step or not, if this test fail
    @Issue("KOVAN-123") // issueID on jira or any issue tracking system.
    @Story("Arrange Act Assert yapisinda birim test yazilmalidir.")
    @Description("Test the isSameMonth function")
    public void testTimeUtils() {
        // 07/01/2016
        final long unixTimestamp = 1467331200;

        // Sometime in august
        final long augustTime = 1470787200;
        Assert.assertEquals(unixTimestamp, TimeUtils.firstOfLastMonthTime(augustTime));
        Assert.assertTrue(TimeUtils.isSameMonth(TimeUtils.firstOfLastMonthTime(augustTime), unixTimestamp));
    }
}
