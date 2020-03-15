package Training;

import java.util.Random;

public class TemperatureService {

    public int getCurrentTemperature() {

        Random r = new Random();
        return r.nextInt(50);
    }
}
