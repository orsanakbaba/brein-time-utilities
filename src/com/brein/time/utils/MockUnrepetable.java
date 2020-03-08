package com.brein.time.utils;

import java.util.Arrays;

public class MockUnrepetable {
    public enum DangerLevel {
        NONE,
        LOW,
        MEDIUM,
        HIGH,
        EXTREME,
        APOCALYPSE
    }

    public enum WeatherConditions {
        SUNNY,
        RAINY,
        WINDY,
        TORNADO,
        TSUNAMI,
        CLOUDY,
        THUNDERSTORM,
        SNOWY,
        HAIL,
    }

    public DangerLevel getDangerLevel(WeatherConditions condition) {
       if(condition == WeatherConditions.SUNNY || condition == WeatherConditions.RAINY)
           return DangerLevel.LOW;
       else if (condition == WeatherConditions.WINDY || condition == WeatherConditions.CLOUDY || condition == WeatherConditions.SNOWY)
           return DangerLevel.MEDIUM;
       else if (condition == WeatherConditions.THUNDERSTORM || condition == WeatherConditions.HAIL)
           return DangerLevel.HIGH;
       else if (condition == WeatherConditions.TORNADO || condition == WeatherConditions.TSUNAMI)
           return DangerLevel.EXTREME;
       return DangerLevel.NONE;
    }

    public DangerLevel getDangerLevel (WeatherConditions[] conditions) {
        if(Arrays.asList(conditions).contains(WeatherConditions.TORNADO) && Arrays.asList(conditions).contains(WeatherConditions.SNOWY) && Arrays.asList(conditions).contains(WeatherConditions.TSUNAMI) && Arrays.asList(conditions).contains(WeatherConditions.SUNNY))
            return DangerLevel.APOCALYPSE;
        else if (Arrays.asList(conditions).contains(WeatherConditions.WINDY) && Arrays.asList(conditions).contains(WeatherConditions.SNOWY))
            return DangerLevel.HIGH;
        if(Arrays.asList(conditions).contains(WeatherConditions.HAIL) && Arrays.asList(conditions).contains(WeatherConditions.RAINY))
            return DangerLevel.MEDIUM;
        return DangerLevel.NONE;

    }

}
