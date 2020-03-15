package Training.FailFirst;

import io.qameta.allure.*;
import org.testng.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class FailFirst {
    //These are the parameters expected to work with the feature.
    @DataProvider(name = "DivisionFeatureParameters")
    public Object[][] divParam() {
        return new Object[][] {
                {10, 5},
                {11, 4},
                {44.3, 12.5},
                {87.31, 0}
        };
    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-122")
    @Story("Write Tests that are testing required features first. Worry about functionality later.")
    @Description("Test testIntDivision_IntDividesInt")
    public void testIntDivision_IntDividesInt() {
        Assert.assertEquals(division(10, 5), 2);
    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-122")
    @Story("Write Tests that are testing required features first. Worry about functionality later.")
    @Description("Test testDoubleDivision_IntDividesInt")
    public void testDoubleDivision_IntDividesInt() {
        Assert.assertEquals(division(11, 4), 2.75);
    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-122")
    @Story("Write Tests that are testing required features first. Worry about functionality later.")
    @Description("Test testDoubleDivision_SmallDoubleDividesDouble")
    public void testDoubleDivision_SmallDoubleDividesDouble() {
        Assert.assertEquals(division((int)44.3, (int)12.5), 3.544);
    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-122")
    @Story("Write Tests that are testing required features first. Worry about functionality later.")
    @Description("Test testDoubleDivision_ZeroDividesInt")
    public void testDoubleDivision_ZeroDividesInt() {
        Assert.assertEquals(division(87, 0), -1);
    }
    //First write the failed test

    //Functionality, is there a question to ask here?
    private int division(int number, int divider) {
        return number / divider;
    }
}
