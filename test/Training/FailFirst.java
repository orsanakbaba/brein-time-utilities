package Training;

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
    public void testIntDivision_IntDividesInt() {
        Assert.assertEquals(division(10, 5), 2);
    }

    @Test
    public void testDoubleDivision_IntDividesInt() {
        Assert.assertEquals(division(11, 4), 2.75);
    }

    @Test
    public void testDoubleDivision_SmallDoubleDividesDouble() {
        Assert.assertEquals(division((int)44.3, (int)12.5), 3.544);
    }

    @Test
    public void testDoubleDivision_ZeroDividesInt() {
        Assert.assertEquals(division(87, 0), -1);
    }
    //First write the failed test

    //Functionality
    private int division(int number, int divider) {
        return number / divider;
    }
}
