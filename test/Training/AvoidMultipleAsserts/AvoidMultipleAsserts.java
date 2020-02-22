package Training.AvoidMultipleAsserts;

import com.brein.time.timer.CountdownTimer;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class AvoidMultipleAsserts {

    private final CountdownTimer timer = new CountdownTimer(1, TimeUnit.SECONDS);

    @Test
    public void timerEnded_StartTimer_Not_Called() {

    }

    @Test
    public void timerEnded_StartTimer_Called_After_Now(){

    }

    @Test
    public void timerEnded_StartTimer_Called_After_Now_Plus_1000ms(){

    }

    @Test
    public void timerEnded_StartTimer_Called_After_Now_Plus_999ms(){

    }

    @Test
    public void timerEnded_StartTimer_Called_After_Now_Plus_1001ms(){

    }

    @Test
    public void timerEnded_StartTimer_Called_After_Now_Plus_100000ms(){

    }

}
