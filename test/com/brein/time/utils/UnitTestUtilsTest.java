package com.brein.time.utils;


import io.qameta.allure.*;
import org.junit.Assert;
import org.junit.Test;

public class UnitTestUtilsTest {

    @Test
    @Owner("hozdemir")
    @Severity(SeverityLevel.CRITICAL)
    @Issue("KOVAN-123")
    @Story("testin içerisnde varsayılan parametrelerin dogrulanmasi")
    @Description("Tests the Default Config Values")
    public void testDefaultConfigValues(){

        UnitTestConfig utc = new UnitTestConfig();
        String issueId = utc.issueId;

        Assert.assertEquals("KOVAN-123",issueId);
    }

    @Test
    @Owner("hozdemir")
    @Severity(SeverityLevel.CRITICAL)
    @Issue("KOVAN-456") //
    @Story("testin içerisindeki geçerli kullanıcı girdiler dogru isleniyor mu")
    @Description("Tests the Explicitly Set Config Values")
    public void testSetConfigValues(){

        String[] args= {"hozdemir", "kritik", "KOVAN-456"};
        UnitTestConfig utc = new UnitTestConfig(args);
        String issueId = utc.issueId;

        Assert.assertEquals("KOVAN-456",issueId);
    }

    @Test (expected = ArrayIndexOutOfBoundsException.class)
    @Owner("hozdemir")
    @Severity(SeverityLevel.CRITICAL)
    @Issue("KOVAN-456")
    @Story("testin icerisnde hatalı kullanıcı girdileri dogru isleniyor mu")
    @Description("Test the config Errors")
    public void testConfigErrors(){

        String[] args= {"KOVAN-456"};
        UnitTestConfig utc = new UnitTestConfig(args);

    }

}