package Training.FailFirst;

import org.junit.*;


public class FailFirst {

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
