package Training.UnnecessaryRepeats;


import com.brein.time.utils.TimeUtils;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import org.junit.*;

import java.time.format.DateTimeFormatter;

public class UnnecessaryRepeatsBadExample {
   @Test
    public void testDateStringPattern1() {
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
       Assert.assertEquals(1480447581L, TimeUtils.dateStringToUnixTimestamp("2016-11-30 04:26:21", formatter, "Asia/Seoul"));
   }

    @Test
    public void testDateStringPattern2() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s");
        Assert.assertEquals(1492348192L, TimeUtils.dateStringToUnixTimestamp("2017-04-16 8:09:52", formatter, "America/Chicago"));
    }

    @Test
    public void testDateStringPattern3() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        Assert.assertEquals(1480447581L, TimeUtils.dateStringToUnixTimestamp("30-11-2016 04:26:21", formatter, "Asia/Seoul"));
    }

    @Test
    public void testDateStringPattern4() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        Assert.assertEquals(1480447581L, TimeUtils.dateStringToUnixTimestamp("11-30-2016 04:26:21", formatter, "Asia/Seoul"));
    }
}
