package Training.GirdiCiktiDegiskenligi;

import com.brein.time.utils.UnitTestUtils;
import io.qameta.allure.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class GirdiCiktiDegiskenligiBadExample {

    @Test
    @Owner("hozdemir")
    @Severity(SeverityLevel.CRITICAL)
    @Issue("KOVAN-123")
    @Story("testin yapılışında değişkenlik gösteren parametreler kullanılmamalıdır.")
    @Description("Test the time of the day")
    public void testGetTimeofDay_Bad() {

        String timeOfDay = UnitTestUtils.getTimeofDay(new Date());
        Assert.assertEquals("Morning", timeOfDay);

    }
}
