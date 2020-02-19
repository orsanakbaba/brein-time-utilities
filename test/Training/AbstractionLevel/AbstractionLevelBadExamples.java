package Training.AbstractionLevel;

import com.brein.time.utils.TimeUtils;
import io.qameta.allure.*;
import org.junit.*;

public class AbstractionLevelBadExamples {

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-129")
    @Story("Birim testlerde koşul ve döngü içeren yapılar bulunmamalıdır.")
    @Description("Tests the Pretty Sting with given seconds ")
    public void checkFormat() {
        String response = TimeUtils.formatUnixTimeStamp(1480446690L);
        Assert.assertTrue(response.indexOf("19:11:30") != -1);
        Assert.assertTrue(response.indexOf("2016/11/29") != -1);
    }
}
