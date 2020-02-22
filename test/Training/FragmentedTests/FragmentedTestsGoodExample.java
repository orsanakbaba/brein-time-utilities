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

public class FragmentedTestsGoodExample {
    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-123")
    @Story("Yekpare olan testler mümkün olduğunca ufak parçalara ayrılmalıdır.")
    @Description("Test checkConvertToUnixTimestamp")
    public void checkConvertToUnixTimestamp() {

    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-123")
    @Story("Yekpare olan testler mümkün olduğunca ufak parçalara ayrılmalıdır.")
    @Description("Test checkConvertToFormattedDateString")
    public void checkConvertToFormattedDateString() {

    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.NORMAL)
    @Issue("KOVAN-123")
    @Story("Yekpare olan testler mümkün olduğunca ufak parçalara ayrılmalıdır.")
    @Description("Test checkSecondsAfterMidnight")
    public void checkSecondsAfterMidnight() {

    }
}
