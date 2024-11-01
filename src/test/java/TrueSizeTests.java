import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import computervision.OpenCvImageService;
import sikuli.SikuliService;

public class TrueSizeTests {

    private static WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testDragCountryAndCompareSize() {
        driver.get("https://www.thetruesize.com/#?borders=1~!MTc3OTcxODU.MTgxOTQ2Mg*MzYwMDAwMDA(MA~!CN*MzI1ODA.MzUyNzI1Mzc)Mw~!US*MA.MTgwMDAwMDA)NA~!ZA*Mjc5ODQ.MzA1NDg0OTE)MA");

        // Drag USA on top of China
        SikuliService.dragAndDrop("usa-full", "china-full", 0.8f);

        // Verify the result
        SikuliService.assertImagePresentOnScreen("usa-china-compare", 0.8f);

        // Drag South Africa on top of the USA-China overlay
        SikuliService.dragAndDrop("south-africa", "usa-china-compare", 0.8f);

        // Verify the final overlay
        SikuliService.assertImagePresentOnScreen("usa-china-south-africa-compare", 0.8f);
    }

    @AfterEach
    public void cleanUp() {
        driver.quit();
    }
}
