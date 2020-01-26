package Training.UnnecessaryRepeats;

import com.brein.time.utils.TimeUtils;
import org.junit.*;

import java.time.format.DateTimeFormatter;


public class UnnecessaryRepeatsGoodExample {

    @Test
    public void testDateStringPattern1() {
        Assert.assertEquals(1480447581L, TimeUtils.dateStringToUnixTimestamp("2016-11-30 04:26:21", getFormat("yyyy-MM-dd HH:mm:ss"), "Asia/Seoul"));
    }

    @Test
    public void testDateStringPattern2() {
        Assert.assertEquals(1492348192L, TimeUtils.dateStringToUnixTimestamp("2017-04-16 8:09:52", getFormat("yyyy-M-d H:m:s"), "America/Chicago"));
    }


    private DateTimeFormatter getFormat(String formatString) {
        return DateTimeFormatter.ofPattern(formatString);
    }
}
