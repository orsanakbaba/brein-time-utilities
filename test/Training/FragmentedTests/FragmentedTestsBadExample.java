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

    }
}
