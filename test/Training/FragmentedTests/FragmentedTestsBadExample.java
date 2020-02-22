package Training.FragmentedTests;

import io.qameta.allure.*;
import org.junit.Test;
import com.brein.time.utils.TimeUtils;
import org.testng.Assert;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class FragmentedTestsBadExample {
    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.NORMAL)
    @Issue("KOVAN-123")
    @Story("Yekpare olan testler mümkün olduğunca ufak parçalara ayrılmalıdır.")
    @Description("Test checkSecondsAfterMidnight")
    public void checkSecondsAfterMidnight() {
        long timeStamp = TimeUtils.dateStringToUnixTimestamp("2019-12-25 00:01:05", "yyyy-MM-dd HH:mm:ss");
        String formattedTimeStamp = TimeUtils.formatUnixTimeStamp(timeStamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss a z");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(formattedTimeStamp + " GMT", formatter);
        long seconds = TimeUtils.getSecondsAfterMidnight(zonedDateTime);
        Assert.assertEquals(seconds, 65);
    }
}
