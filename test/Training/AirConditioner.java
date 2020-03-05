package Training;

public class AirConditioner {

    private Config config;

    public void initialize(int currentTemperature) {
        if (currentTemperature > 20) {
            config = Config.COOLING;
        } else {
            config = Config.HEATING;
        }
    }

    public Config getConfig() {
        return config;
    }

    public enum Config {
        HEATING,
        COOLING
    }
}
