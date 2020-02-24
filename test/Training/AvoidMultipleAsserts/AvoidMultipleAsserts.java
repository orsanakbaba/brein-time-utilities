package Training.AvoidMultipleAsserts;

import com.brein.time.timer.CountdownTimer;
import io.qameta.allure.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class AvoidMultipleAsserts {

    private final CountdownTimer timer = new CountdownTimer(1, TimeUnit.SECONDS);

    @Test
    @Owner("oakbaba") // Unit test writer name
    @Severity(SeverityLevel.CRITICAL)   // Developer should tag severity of test from one of the following : BLOCKER,CRITICAL,NORMAL,MINOR,TRIVIAL. TeamLead ot Tech.Manager may use this information to decide to step or not, if this test fail
    @Issue("KOVAN-123") // issueID on jira or any issue tracking system.
    @Story("Birden fazla assertiondan kaçınılmalıdır.")
    @Description("Test the timerEnded_StartTimer_Not_Called function")
    public void timerEnded_StartTimer_Not_Called() {

    }

    @Test
    @Owner("oakbaba") // Unit test writer name
    @Severity(SeverityLevel.CRITICAL)   // Developer should tag severity of test from one of the following : BLOCKER,CRITICAL,NORMAL,MINOR,TRIVIAL. TeamLead ot Tech.Manager may use this information to decide to step or not, if this test fail
    @Issue("KOVAN-123") // issueID on jira or any issue tracking system.
    @Story("Birden fazla assertiondan kaçınılmalıdır.")
    @Description("Test the timerEnded_StartTimer_Called_After_Now function")
    public void timerEnded_StartTimer_Called_After_Now(){

    }

    @Test
    @Owner("oakbaba") // Unit test writer name
    @Severity(SeverityLevel.CRITICAL)   // Developer should tag severity of test from one of the following : BLOCKER,CRITICAL,NORMAL,MINOR,TRIVIAL. TeamLead ot Tech.Manager may use this information to decide to step or not, if this test fail
    @Issue("KOVAN-123") // issueID on jira or any issue tracking system.
    @Story("Birden fazla assertiondan kaçınılmalıdır.")
    @Description("Test the timerEnded_StartTimer_Called_After_Now_Plus_1000ms function")
    public void timerEnded_StartTimer_Called_After_Now_Plus_1000ms(){

    }

    @Test
    @Owner("oakbaba") // Unit test writer name
    @Severity(SeverityLevel.CRITICAL)   // Developer should tag severity of test from one of the following : BLOCKER,CRITICAL,NORMAL,MINOR,TRIVIAL. TeamLead ot Tech.Manager may use this information to decide to step or not, if this test fail
    @Issue("KOVAN-123") // issueID on jira or any issue tracking system.
    @Story("Birden fazla assertiondan kaçınılmalıdır.")
    @Description("Test the timerEnded_StartTimer_Called_After_Now_Plus_999ms function")
    public void timerEnded_StartTimer_Called_After_Now_Plus_999ms(){

    }

    @Test
    @Owner("hozdemir") // Unit test writer name
    @Severity(SeverityLevel.CRITICAL)   // Developer should tag severity of test from one of the following : BLOCKER,CRITICAL,NORMAL,MINOR,TRIVIAL. TeamLead ot Tech.Manager may use this information to decide to step or not, if this test fail
    @Issue("KOVAN-123") // issueID on jira or any issue tracking system.
    @Story("Arrange Act Assert yapisinda birim test yazilmalidir.")
    @Description("Test the timerEnded_StartTimer_Called_After_Now_Plus_1001ms function")
    public void timerEnded_StartTimer_Called_After_Now_Plus_1001ms(){

    }

    @Test
    @Owner("hozdemir") // Unit test writer name
    @Severity(SeverityLevel.CRITICAL)   // Developer should tag severity of test from one of the following : BLOCKER,CRITICAL,NORMAL,MINOR,TRIVIAL. TeamLead ot Tech.Manager may use this information to decide to step or not, if this test fail
    @Issue("KOVAN-123") // issueID on jira or any issue tracking system.
    @Story("Arrange Act Assert yapisinda birim test yazilmalidir.")
    @Description("Test the timerEnded_StartTimer_Called_After_Now_Plus_100000ms function")
    public void timerEnded_StartTimer_Called_After_Now_Plus_100000ms(){

    }

}
