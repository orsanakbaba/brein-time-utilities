package com.brein.time.utils;

import java.util.Date;


public class UnitTestUtils {


    public static int count = 0;


    //Testler birbirinden bağımsız olmalıdır
    public int increaseCountNumber(){
        return ++count;
    }

    public int decreaseCountNumber(){
        return --count;
    }


    public static String getTimeofDay(Date time){


        if (time.getHours() >= 0 && time.getHours() < 6)
        {
            return "Night";
        }
        if (time.getHours() >= 6 && time.getHours() < 12)
        {
            return "Morning";
        }
        if (time.getHours() >= 12 && time.getHours() < 18)
        {
            return "Afternoon";
        }
        return "Evening";

    }


}
