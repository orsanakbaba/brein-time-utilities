package com.brein.time.AllureExamples;

import io.qameta.allure.*;
import org.junit.Assert;
import org.junit.Test;

public class AllureExamplesTestNG {

    @Test
    @Owner("Cooler Thanos")
    @Severity(SeverityLevel.CRITICAL)
    @Step("Test1")
    @TmsLink("ARCEP-622")
    public void assertTrue() {
        Assert.assertTrue(false);
    }
}
