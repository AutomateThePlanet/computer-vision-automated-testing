import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import computervision.OpenCvImageService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class TrueSizeTestsInLambdaTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        String username = System.getenv("LT_USERNAME");
        String authkey = System.getenv("LT_ACCESSKEY");
        String hub = "@hub.lambdatest.com/wd/hub";

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("browserVersion", "latest");

        HashMap<String, Object> ltOptions = new HashMap<>();
        ltOptions.put("user", username);
        ltOptions.put("accessKey", authkey);
        ltOptions.put("platformName", "Windows 10");

        capabilities.setCapability("LT:Options", ltOptions);

        driver = new RemoteWebDriver(new URL("https://" + username + ":" + authkey + hub), capabilities);
        driver.manage().window().maximize();
        OpenCvImageService.setWebDriver(driver);
    }

    @Test
    public void testDragCountryAndCompareSize() {
        driver.get("https://www.thetruesize.com/#?borders=1~!MTc3OTcxODU.MTgxOTQ2Mg*MzYwMDAwMDA(MA~!CN*MzI1ODA.MzUyNzI1Mzc)Mw~!US*MA.MTgwMDAwMDA)NA~!ZA*Mjc5ODQ.MzA1NDg0OTE)MA");

        // Step 1: Drag the USA on top of China
        OpenCvImageService.dragAndDrop("usa-full", "china-full");

        // Step 2: Verify the result of USA overlaying China
        OpenCvImageService.assertImagePresentOnScreen("usa-china-compare", 0.8f);

        // Step 3: Drag South Africa on top of the USA-China overlay
        OpenCvImageService.dragAndDrop("south-africa", "usa-china-compare");

        // Step 4: Verify the result of South Africa overlaying USA-China
        OpenCvImageService.assertImagePresentOnScreen("usa-china-south-africa-compare", 0.8f);
    }

    @AfterEach
    public void cleanUp() {
        if (driver != null) {
            driver.quit();
        }
    }
}
