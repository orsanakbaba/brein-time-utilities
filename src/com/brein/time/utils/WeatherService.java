package com.brein.time.utils;

import java.util.Random;

public class WeatherService {

    public MockUnrepetable.WeatherConditions getWeather() {
        switch (getRandomNumber(9)) {
            case 0:
                return MockUnrepetable.WeatherConditions.SUNNY;
            case 1:
                return MockUnrepetable.WeatherConditions.RAINY;
            case 2:
                return MockUnrepetable.WeatherConditions.WINDY;
            case 3:
                return MockUnrepetable.WeatherConditions.TORNADO;
            case 4:
                return MockUnrepetable.WeatherConditions.TSUNAMI;
            case 5:
                return MockUnrepetable.WeatherConditions.CLOUDY;
            case 6:
                return MockUnrepetable.WeatherConditions.THUNDERSTORM;
            case 7:
                return MockUnrepetable.WeatherConditions.SNOWY;
            case 8:
                return MockUnrepetable.WeatherConditions.HAIL;
        }
        return null;
    }

    public MockUnrepetable.WeatherConditions[] getMultipleWeathers() {
        int chosen = getRandomNumber(10000);
        if(chosen < 3577)
            return new MockUnrepetable.WeatherConditions[]{MockUnrepetable.WeatherConditions.WINDY, MockUnrepetable.WeatherConditions.SNOWY};
        else if (chosen >= 3577 && chosen < 6500)
            return new MockUnrepetable.WeatherConditions[]{MockUnrepetable.WeatherConditions.HAIL, MockUnrepetable.WeatherConditions.RAINY};
        else if (chosen >= 6500 && chosen < 9999)
            return new MockUnrepetable.WeatherConditions[]{MockUnrepetable.WeatherConditions.RAINY, MockUnrepetable.WeatherConditions.THUNDERSTORM};
        //Probability of activation of the line below is 1 out of 10k.
        else if (chosen == 9999)
            return new MockUnrepetable.WeatherConditions[]{MockUnrepetable.WeatherConditions.TORNADO, MockUnrepetable.WeatherConditions.SNOWY, MockUnrepetable.WeatherConditions.TSUNAMI, MockUnrepetable.WeatherConditions.SUNNY};
        return null;
    }

    //Utils, inside a folder called utils, Doin' utils stuff.
    private int getRandomNumber(int fIndex) {
        Random rnd = new Random();
        return rnd.nextInt(fIndex);
    }
}
