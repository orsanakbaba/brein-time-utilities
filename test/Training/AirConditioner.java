package Training;

public class AirConditioner {

    private final TemperatureService service;
    private Config config;

    public AirConditioner(TemperatureService service) {
        this.service = service;
    }

    public void initialize() {
        if (service.getCurrentTemperature() > 20) {
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
