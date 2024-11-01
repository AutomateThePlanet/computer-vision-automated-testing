package computervision;

import nu.pattern.OpenCV;
import org.junit.jupiter.api.Assertions;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class OpenCvImageService {
    private static final String RESOURCE_PATH = "images/";
    private static WebDriver driver;

    // Initialize OpenCV once when the class is loaded
    static {
        OpenCV.loadLocally();
    }

    /**
     * Sets the WebDriver instance to be used by the OpenCvImageService.
     * @param webDriver the WebDriver instance
     */
    public static void setWebDriver(WebDriver webDriver) {
        driver = webDriver;
    }

    /**
     * Loads an image file from the resources directory by name, automatically checking common image extensions.
     * @param imageName the base name of the image file (without extension)
     * @return the path to the located image file in the resources
     */
    private static String getImagePathFromResources(String imageName) {
        List<String> extensions = Arrays.asList(".png", ".jpg", ".jpeg");

        for (String extension : extensions) {
            String resourcePath = RESOURCE_PATH + imageName + extension;
            if (OpenCvImageService.class.getClassLoader().getResource(resourcePath) != null) {
                return OpenCvImageService.class.getClassLoader().getResource(resourcePath).getPath();
            }
        }
        throw new RuntimeException("Image file not found in resources with name: " + imageName);
    }

    /**
     * Finds the location of an image on the screen using OpenCV template matching.
     * @param imageName the name of the image to locate in resources (without extension)
     * @return the location (Point) of the image on the screen
     */
    public static org.openqa.selenium.Point getLocation(String imageName) {
        checkWebDriver();
        String templatePath = getImagePathFromResources(imageName);
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        Mat template = Imgcodecs.imread(templatePath);
        Mat source = Imgcodecs.imread(screenshotFile.getPath());

        if (template.empty() || source.empty()) {
            throw new RuntimeException("Failed to load images for OpenCV processing.");
        }

        Mat result = new Mat();
        Imgproc.matchTemplate(source, template, result, Imgproc.TM_CCOEFF_NORMED);

        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        return new org.openqa.selenium.Point((int) mmr.maxLoc.x, (int) mmr.maxLoc.y);
    }

    /**
     * Asserts that the specified image is present on the screen.
     * @param imageName the name of the image to verify in resources (without extension)
     * @param similarityThreshold the threshold for similarity (between 0 and 1)
     */
    public static void assertImagePresentOnScreen(String imageName, float similarityThreshold) {
        checkWebDriver();
        String templatePath = getImagePathFromResources(imageName);
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        Mat template = Imgcodecs.imread(templatePath);
        Mat source = Imgcodecs.imread(screenshotFile.getPath());

        if (template.empty() || source.empty()) {
            throw new RuntimeException("Failed to load images for OpenCV processing.");
        }

        Mat result = new Mat();
        Imgproc.matchTemplate(source, template, result, Imgproc.TM_CCOEFF_NORMED);

        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        if (mmr.maxVal < similarityThreshold) {
            Assertions.fail("Image not found on screen with sufficient similarity: " + imageName);
        }
    }

    /**
     * Clicks on the image located on the screen by moving to the identified location and clicking.
     * @param imageName the name of the image to click in resources (without extension)
     */
    public static void click(String imageName) {
        checkWebDriver();
        var location = getLocation(imageName);
        Actions actions = new Actions(driver);
        actions.moveByOffset(location.x + 10, location.y + 10) // Offset for accurate clicking
                .click()
                .perform();
    }

    /**
     * Drags an image from its location on the screen to the location of another image.
     * @param sourceImageName the name of the source image in resources (without extension)
     * @param targetImageName the name of the target image in resources (without extension)
     */
    public static void dragAndDrop(String sourceImageName, String targetImageName) {
        checkWebDriver();
        var sourceLocation = getLocation(sourceImageName);
        var targetLocation = getLocation(targetImageName);
        Actions actions = new Actions(driver);

        actions.moveByOffset(sourceLocation.x + 10, sourceLocation.y + 10)
                .clickAndHold()
                .moveByOffset(targetLocation.x - sourceLocation.x, targetLocation.y - sourceLocation.y)
                .release()
                .perform();
    }

    /**
     * Hovers over the image located on the screen by moving to the identified location.
     * @param imageName the name of the image to hover over in resources (without extension)
     */
    public static void hover(String imageName) {
        checkWebDriver();
        var location = getLocation(imageName);
        Actions actions = new Actions(driver);
        actions.moveByOffset(location.x + 10, location.y + 10)
                .perform();
    }

    /**
     * Checks if the WebDriver is set and throws an exception if it's not initialized.
     */
    private static void checkWebDriver() {
        if (driver == null) {
            throw new IllegalStateException("WebDriver has not been set. Call setWebDriver() before using this service.");
        }
    }
}
