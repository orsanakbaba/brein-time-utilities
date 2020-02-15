package Training.UnnecessaryRepeats;

import com.brein.time.utils.TimeUtils;
import io.qameta.allure.*;
import org.junit.*;

import java.time.format.DateTimeFormatter;


public class UnnecessaryRepeatsGoodExample {

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-123")
    @Story("Gereksiz tekrarlardan kaçınılmalıdır.")
    @Description("Test testDateStringPattern_Iteration1")
    public void testDateStringPattern1() {
        Assert.assertEquals(1480447581L, TimeUtils.dateStringToUnixTimestamp("2016-11-30 04:26:21", getFormat("yyyy-MM-dd HH:mm:ss"), "Asia/Seoul"));
    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-123")
    @Story("Yekpare olan testler mümkün olduğunca ufak parçalara ayrılmalıdır.")
    @Description("Test testDateStringPattern_Iteration2")
    public void testDateStringPattern2() {
        Assert.assertEquals(1492348192L, TimeUtils.dateStringToUnixTimestamp("2017-04-16 8:09:52", getFormat("yyyy-M-d H:m:s"), "America/Chicago"));
    }


    private DateTimeFormatter getFormat(String formatString) {
        return DateTimeFormatter.ofPattern(formatString);
    }
}
