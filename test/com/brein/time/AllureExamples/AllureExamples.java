package com.brein.time.AllureExamples;

import io.qameta.allure.*;
import org.junit.Assert;
import org.junit.Test;

//These classes are for showing some common allure annotations.
@Epic("Allure Examples")
public class AllureExamples {
    @Test
    @Owner("Thanos")
    @Severity(SeverityLevel.CRITICAL)
    @Step("Test1")
    @TmsLink("ARCEP-611")
    public void isTrue() {
        Assert.assertTrue(true);
    }
}
