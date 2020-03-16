package Training.Mocking.NonDeterministic;

import org.junit.Assert;
import org.junit.Test;

// Bu testler her çalıştırıldığında farklı sonuçlar üretir.
public class AirConditionerWrongTest {

    private TemperatureService service;

    @Test
    public void testAirConditionerInitialize_mustBeHeating() {

        service = new TemperatureService();
        AirConditioner airConditioner = new AirConditioner(service);
        airConditioner.initialize();

        Assert.assertEquals(AirConditioner.Config.HEATING, airConditioner.getConfig());
    }

    @Test
    public void testAirConditionerInitialize_mustBeCooling() {

        service = new TemperatureService();
        AirConditioner airConditioner = new AirConditioner(service);
        airConditioner.initialize();

        Assert.assertEquals(AirConditioner.Config.COOLING, airConditioner.getConfig());
    }
}
