package Training.FailFirst;

import org.junit.*;


public class FailFirst {

    @Test
    public void testIntDivision_IntDividesInt() {

    }

    @Test
    public void testDoubleDivision_IntDividesInt() {

    }

    @Test
    public void testDoubleDivision_SmallDoubleDividesDouble() {

    }

    @Test
    public void testDoubleDivision_ZeroDividesInt() {

    }
    //First write the failed test

    //Functionality
    private int division(int number, int divider) {
        return number / divider;
    }
}
