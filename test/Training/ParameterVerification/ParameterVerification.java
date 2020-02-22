package Training.ParameterVerification;

import com.brein.time.utils.UnitTestConfig;
import io.qameta.allure.*;
import org.junit.Assert;
import org.junit.Test;

public class ParameterVerification {
    @Test
    @Owner("hozdemir")
    @Severity(SeverityLevel.CRITICAL)
    @Issue("KOVAN-123")
    @Story("testin içerisindeki varsayılan parametrelerin dogrulanmasi")
    @Description("Tests the Default Config Values")
    public void testDefaultConfigValues(){

    }

    @Test
    @Owner("hozdemir")
    @Severity(SeverityLevel.CRITICAL)
    @Issue("KOVAN-456") //
    @Story("testin içerisindeki geçerli kullanıcı girdiler dogru isleniyor mu")
    @Description("Tests the Explicitly Set Config Values")
    public void testSetConfigValues(){

    }

    @Test (expected = ArrayIndexOutOfBoundsException.class)
    @Owner("hozdemir")
    @Severity(SeverityLevel.CRITICAL)
    @Issue("KOVAN-456")
    @Story("testin icerisindeki hatalı kullanıcı girdileri dogru isleniyor mu")
    @Description("Test the config Errors")
    public void testConfigErrors(){

    }
}
