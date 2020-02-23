package Training.WhenToWriteTests;

import com.brein.time.utils.WhenToWriteTestsSource;
import io.qameta.allure.*;
import org.junit.Test;
import org.testng.Assert;

import javax.swing.*;
import java.awt.*;

public class WhenNOTtoWriteTests {

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-128")
    @Story("Birim Test / Maliyet ilişkisi göz önünde bulundurulmalıdır.")
    @Description("Test checkConvertToUnixTimestamp")
    public void testAgeGetterSetter() {
        WhenToWriteTestsSource test = new WhenToWriteTestsSource();
        test.setAge(25);

        Assert.assertEquals(test.getAge(), 25);
    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-123")
    @Story("Birim Test / Maliyet ilişkisi göz önünde bulundurulmalıdır.")
    @Description("Test checkConvertToUnixTimestamp")
    public void testNameGetterSetter() {
        WhenToWriteTestsSource test = new WhenToWriteTestsSource();
        test.setName("Thanos");

        Assert.assertEquals(test.getName(), "Thanos");
    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-123")
    @Story("Birim Test / Maliyet ilişkisi göz önünde bulundurulmalıdır.")
    @Description("Test checkConvertToUnixTimestamp")
    public void testGUIText() {
        WhenToWriteTestsSource test = new WhenToWriteTestsSource();
        JTextArea area = new JTextArea();
        test.setTextAreaText(area, "PressForThanos");

        Assert.assertEquals(area.getText(), "PressForThanos");
    }

    @Test
    @Owner("msongur")
    @Severity(SeverityLevel.MINOR)
    @Issue("KOVAN-123")
    @Story("Birim Test / Maliyet ilişkisi göz önünde bulundurulmalıdır.")
    @Description("Test checkConvertToUnixTimestamp")
    public void testConstructButton() {
        WhenToWriteTestsSource test = new WhenToWriteTestsSource();
        JButton btn = new JButton();
        test.constructButtonType1(btn);
        Assert.assertEquals(btn.getBackground(), Color.CYAN);
        Assert.assertEquals(btn.getFont(), Font.getFont(Font.SANS_SERIF));
        Assert.assertEquals(btn.getWidth(), 400);
        Assert.assertEquals(btn.getHeight(), 500);
    }
}
