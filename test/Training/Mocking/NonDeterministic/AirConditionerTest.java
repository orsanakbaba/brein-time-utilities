package Training.Mocking.NonDeterministic;

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

        AirConditioner airConditioner = new AirConditioner(service);
        airConditioner.initialize();

        Assert.assertEquals(AirConditioner.Config.HEATING, airConditioner.getConfig());
    }

    @Test
    public void testAirConditionerInitialize_mustBeCooling() {
        when(service.getCurrentTemperature()).thenReturn(30);

        AirConditioner airConditioner = new AirConditioner(service);
        airConditioner.initialize();

        Assert.assertEquals(AirConditioner.Config.COOLING, airConditioner.getConfig());
    }
}
