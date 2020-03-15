package Training;

import com.brein.time.utils.UnitTestUtils;
import io.qameta.allure.*;
import org.junit.Assert;
import org.junit.Test;

public class BagimsizBirimTest {
    @Test
    @Owner("hozdemir")
    @Severity(SeverityLevel.NORMAL)
    @Issue("KOVAN-125")
    @Story("Birim test aynı zamanda bağımsız olmalıdır. testler birbirini etkilememelidir.")
    @Description("Test the increase count number")
    public void testIncreaseCountNumberReturnTrue() {
        UnitTestUtils hu = new UnitTestUtils();
        int result = hu.increaseCountNumber();

        Assert.assertEquals(1,result);
    }

    @Test
    @Owner("hozdemir")
    @Severity(SeverityLevel.NORMAL)
    @Issue("KOVAN-125")
    @Story("Birim test aynı zamanda bağımsız olmalıdır. testler birbirini etkilememelidir.")
    @Description("Test the increase count number")
    public void testDecreaseCountNumberReturnTrue() {
        UnitTestUtils hu = new UnitTestUtils();
        int result = hu.decreaseCountNumber();

        Assert.assertEquals(-1,result);
    }
}
