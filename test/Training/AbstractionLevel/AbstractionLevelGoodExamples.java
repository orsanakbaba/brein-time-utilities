package Training.AbstractionLevel;

import com.brein.time.utils.TimeUtils;
import org.junit.*;
import io.qameta.allure.*;

public class AbstractionLevelGoodExamples {

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.NORMAL)
    @Issue("KOVAN-129")
    @Story("Birim testlerde koşul ve döngü içeren yapılar bulunmamalıdır.")
    @Description("Tests the Pretty Sting with given seconds ")
    public void checkFormatHourToSecond() {
        String response = TimeUtils.formatUnixTimeStamp(1480446690L);
        Assert.assertEquals(response.contains("19:11:30"), true);
    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.NORMAL)
    @Issue("KOVAN-129")
    @Story("Birim testlerde koşul ve döngü içeren yapılar bulunmamalıdır.")
    @Description("Tests the Pretty Sting with given seconds ")
    public void checkFormatDayToYear() {
        String response = TimeUtils.formatUnixTimeStamp(1480446690L);
        Assert.assertEquals(response.contains("2016/11/29"), true);
    }
}
