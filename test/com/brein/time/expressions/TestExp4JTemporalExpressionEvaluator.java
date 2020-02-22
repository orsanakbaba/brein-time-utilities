package com.brein.time.expressions;

import com.brein.time.utils.TimeUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonMap;

public class TestExp4JTemporalExpressionEvaluator {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testErrorForInvalidUnitsBasedOnLowestTimeGranularity() {
        final Exp4JTemporalExpressionEvaluator eval = new Exp4JTemporalExpressionEvaluator();
        eval.init(singletonMap(Exp4JTemporalExpressionEvaluator.LOWEST_TIME_GRANULARITY, TimeUnit.SECONDS));

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Unable to parse the formula: 1ms");

        eval.addFormula("oneMillisecond", "1ms");

        eval.close();
    }

    @Test
    public void testLowestTimeGranularity() {
        final Exp4JTemporalExpressionEvaluator eval = new Exp4JTemporalExpressionEvaluator();
        eval.init(singletonMap(Exp4JTemporalExpressionEvaluator.LOWEST_TIME_GRANULARITY, TimeUnit.SECONDS));

        eval.addFormula("oneSecond", "1sec");
        Assert.assertEquals(1L, eval.evaluate("oneSecond"));

        eval.addFormula("oneMinute", "1min");
        Assert.assertEquals(60L, eval.evaluate("oneMinute"));

        eval.addFormula("oneHour", "1h");
        Assert.assertEquals(60L * 60L, eval.evaluate("oneHour"));

        eval.close();
    }

    @Ignore
    @Test
    public void testToFunctions() {
        final Exp4JTemporalExpressionEvaluator eval = new Exp4JTemporalExpressionEvaluator();
        eval.init(singletonMap(Exp4JTemporalExpressionEvaluator.LOWEST_TIME_GRANULARITY, TimeUnit.SECONDS));

        eval.addFormula("oneMinuteToMinutes", "toMinutes(1min)");
        Assert.assertEquals(1L, eval.evaluate("oneMinuteToMinutes"));

        eval.addFormula("oneHourToMinutes", "toMinutes(1h)");
        Assert.assertEquals(60L, eval.evaluate("oneHourToMinutes"));

        eval.addFormula("oneHourToHours", "toHours(1h)");
        Assert.assertEquals(1L, eval.evaluate("oneHourToHours"));

        eval.addFormula("oneHourToDays", "toDays(48h)");
        Assert.assertEquals(2L, eval.evaluate("oneHourToDays"));

        eval.close();
    }

    @Test
    public void testVariableBinding() {
        final Exp4JTemporalExpressionEvaluator eval = new Exp4JTemporalExpressionEvaluator();
        eval.init(singletonMap(Exp4JTemporalExpressionEvaluator.LOWEST_TIME_GRANULARITY, TimeUnit.SECONDS));

        final long now = TimeUtils.now();

        eval.addFormula("addFiveSecs", "t + 5sec", Collections.singleton("t"));
        Assert.assertEquals(now + 5L, eval.evaluate("addFiveSecs", singletonMap(
                "t", now
        )));

        eval.addFormula("addFiveMins", "t + 5min", Collections.singleton("t"));
        Assert.assertEquals(now + (5L * 60L), eval.evaluate("addFiveMins", singletonMap(
                "t", now
        )));

        eval.addFormula("addFiveSecs", "now() + 5sec", Collections.singleton("t"));
        Assert.assertEquals(now + 5L, eval.evaluate("addFiveSecs"));

        eval.close();
    }
}
