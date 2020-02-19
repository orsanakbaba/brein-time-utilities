package Training;

import com.brein.time.utils.UnitTestUtils;
import io.qameta.allure.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class GirdiCiktiDegiskenligiGoodExample {

    @Test
    @Owner("hozdemir")
    @Severity(SeverityLevel.CRITICAL)
    @Issue("KOVAN-123")
    @Story("testin yapılışında değişkenlik gösteren parametreler kullanılmamalıdır.")
    @Description("Test the time of the day")
    public void testGetTimeofDay_Good() {

        Date date = new Date(2020,01,28,7,52);
        String timeOfDay = UnitTestUtils.getTimeofDay(date);

        Assert.assertEquals("Morning", timeOfDay);

    }
}
