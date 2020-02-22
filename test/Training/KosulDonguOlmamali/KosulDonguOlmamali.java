package Training.KosulDonguOlmamali;

import com.brein.time.utils.TimeUtils;
import io.qameta.allure.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class KosulDonguOlmamali {
    @Test
    @Owner("hozdemir")
    @Severity(SeverityLevel.CRITICAL)
    @Issue("KOVAN-123")
    @Story("Birim testlerde koşul ve döngü içeren yapılar bulunmamalıdır.")
    @Description("Tests the Pretty Sting with given seconds ")
    public void testSecondsToPrettyStringWitIFcase() {

        Random random = new Random();
        int seconds = random.nextInt(1000);
        String string = TimeUtils.secondsToPrettyString(seconds);

        if(seconds>=120){
            Assert.assertTrue(string.endsWith("minutes"));
        }else{
            Assert.assertTrue(string.endsWith("seconds"));
        }
    }
}