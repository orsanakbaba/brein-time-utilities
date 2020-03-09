package Training.Mocking.HardToRepeat;

import io.qameta.allure.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.when;


import com.brein.time.utils.MockUnrepetable;
import com.brein.time.utils.WeatherService;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HardToRepeat {

    @Mock
    private WeatherService weatherService;

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.NORMAL)
    @Issue("KOVAN-225")
    @Story("Mocking yeniden yaratması zor veya imkansız durumları yapay olarak tekrar oluşturmak için kullanılabilir.")
    @Description("Test testDangerLevelLOW")
    public void testDangerLevelLOW() {
        when(weatherService.getWeather()).thenReturn(MockUnrepetable.WeatherConditions.RAINY);

        MockUnrepetable service = new MockUnrepetable();
        MockUnrepetable.WeatherConditions artificialWeather =  weatherService.getWeather();

        Assert.assertEquals(MockUnrepetable.DangerLevel.LOW, service.getDangerLevel(artificialWeather));
    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.NORMAL)
    @Issue("KOVAN-225")
    @Story("Mocking yeniden yaratması zor veya imkansız durumları yapay olarak tekrar oluşturmak için kullanılabilir.")
    @Description("Test testMultipleWeatherDangerLevelHIGH")
    public void testMultipleWeatherDangerLevelHIGH() {
        when(weatherService.getMultipleWeathers()).thenReturn(new MockUnrepetable.WeatherConditions[]{MockUnrepetable.WeatherConditions.WINDY, MockUnrepetable.WeatherConditions.SNOWY});

        MockUnrepetable service = new MockUnrepetable();
        MockUnrepetable.WeatherConditions[] artificialWeather = weatherService.getMultipleWeathers();

        Assert.assertEquals(MockUnrepetable.DangerLevel.HIGH, service.getDangerLevel(artificialWeather));
    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.NORMAL)
    @Issue("KOVAN-225")
    @Story("Mocking yeniden yaratması zor veya imkansız durumları yapay olarak tekrar oluşturmak için kullanılabilir.")
    @Description("Test testMultipleWeatherDangerLevelHIGH")
    public void TESTAPOCALYPSE() {
        //This test is written to test APOCALYPSE Danger level. What kind of sane person uses a danger level indicator to see how dangerous the APOCALYPSE
        //is remains to be seen but here it is. (Advice: This func is not recommended to use during an actual apocalypse. If you think you are experiencing one,
        // we would strongly recommend doing other physical endeavours such as running, screaming, suiciding or sacrificing a few goats to appease our lord c'tulhu.)

        //Imitate apocalypse.
        when(weatherService.getMultipleWeathers()).thenReturn(new MockUnrepetable.WeatherConditions[]{MockUnrepetable.WeatherConditions.TORNADO, MockUnrepetable.WeatherConditions.SNOWY, MockUnrepetable.WeatherConditions.TSUNAMI, MockUnrepetable.WeatherConditions.SUNNY});

        //Get apocalypse
        MockUnrepetable service = new MockUnrepetable();
        MockUnrepetable.WeatherConditions[] apocalypticWeather = weatherService.getMultipleWeathers();

        Assert.assertEquals(MockUnrepetable.DangerLevel.APOCALYPSE,service.getDangerLevel(apocalypticWeather));

        System.out.println("Successfully mocked apocalypse. Good job.");
    }
}
