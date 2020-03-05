package Training;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AirConditionerTest {

    @Mock
    private TemperatureService service;

    @Test
    public void testAirConditionerInitialize_mustBeHeating() {
        when(service.getCurrentTemperature()).thenReturn(15);

        int currentTemperature = service.getCurrentTemperature();
        AirConditioner airConditioner = new AirConditioner();
        airConditioner.initialize(currentTemperature);

        Assert.assertEquals(AirConditioner.Config.HEATING, airConditioner.getConfig());
    }

    @Test
    public void testAirConditionerInitialize_mustBeCooling() {
        when(service.getCurrentTemperature()).thenReturn(30);

        int currentTemperature = service.getCurrentTemperature();
        AirConditioner airConditioner = new AirConditioner();
        airConditioner.initialize(currentTemperature);

        Assert.assertEquals(AirConditioner.Config.COOLING, airConditioner.getConfig());
    }
}
