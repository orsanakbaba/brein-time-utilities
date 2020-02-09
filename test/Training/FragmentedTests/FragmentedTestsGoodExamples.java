package Training.FragmentedTests;

import org.junit.Test;
import com.brein.time.utils.TimeUtils;
import org.testng.Assert;
import io.qameta.allure.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class FragmentedTestsGoodExamples {
    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-123")
    @Story("Yekpare olan testler mümkün olduğunca ufak parçalara ayrılmalıdır.")
    @Description("Test checkConvertToUnixTimestamp")
    public void checkConvertToUnixTimestamp() {
        long timeStamp = TimeUtils.dateStringToUnixTimestamp("2019-12-25 00:01:05", "yyyy-MM-dd HH:mm:ss");
        Assert.assertEquals(timeStamp, 1577232065);
    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-123")
    @Story("Yekpare olan testler mümkün olduğunca ufak parçalara ayrılmalıdır.")
    @Description("Test checkConvertToFormattedDateString")
    public void checkConvertToFormattedDateString() {
        String formattedTimeStamp = TimeUtils.formatUnixTimeStamp(1577232065);
        Assert.assertEquals(formattedTimeStamp, "2019/12/25 00:01:05");
    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.NORMAL)
    @Issue("KOVAN-123")
    @Story("Yekpare olan testler mümkün olduğunca ufak parçalara ayrılmalıdır.")
    @Description("Test checkSecondsAfterMidnight")
    public void checkSecondsAfterMidnight() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss a z");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2019/12/25 00:01:05" + " GMT", formatter);
        long seconds = TimeUtils.getSecondsAfterMidnight(zonedDateTime);
        Assert.assertEquals(seconds, 65);
    }
}
