package sikuli;

import org.junit.jupiter.api.Assertions;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SikuliService {
    private static final String LOCAL_STORAGE_PATH = "images/"; // images folder within resources

    /**
     * Drag and drop from one image location to another.
     */
    public static void dragAndDrop(String dragFromFileName, String dragToFileName, float similarity) {
        String dragFromPath = getSikuliFileByName(dragFromFileName);
        String dragToPath = getSikuliFileByName(dragToFileName);

        Screen screen = new Screen();
        try {
            Pattern sourceElement = new Pattern(dragFromPath).similar(similarity);
            Pattern targetElement = new Pattern(dragToPath).similar(similarity);

            Match sourceMatch = screen.find(sourceElement);
            Match targetMatch = screen.find(targetElement);

            screen.dragDrop(sourceMatch, targetMatch);
        } catch (FindFailed e) {
            e.printStackTrace();
            System.out.println("Failed to drag and drop");
        }
    }

    /**
     * Drag an image to a specified location.
     */
    public static void drag(String dragFromFileName, float similarity) {
        String dragFromPath = getSikuliFileByName(dragFromFileName);

        Screen screen = new Screen();
        try {
            Pattern sourceElement = new Pattern(dragFromPath).similar(similarity);
            Match sourceMatch = screen.find(sourceElement);
            screen.drag(sourceMatch);
        } catch (FindFailed e) {
            e.printStackTrace();
            System.out.println("Failed to drag");
        }
    }

    /**
     * Click on the specified image.
     */
    public static void clickOnImage(String sourceFileName, float similarity) {
        String clickSikuliFile = getSikuliFileByName(sourceFileName);

        Screen screen = new Screen();
        try {
            Pattern clickElement = new Pattern(clickSikuliFile).similar(similarity);
            Match sourceMatch = screen.find(clickElement);
            screen.click(sourceMatch);
        } catch (FindFailed e) {
            e.printStackTrace();
            System.out.println("Failed to click on image");
        }
    }

    /**
     * Assert that the specified image is present on the screen.
     */
    public static void assertImagePresentOnScreen(String sourceFileName, float similarity) {
        String presentSikuliFile = getSikuliFileByName(sourceFileName);

        Screen screen = new Screen();
        try {
            Pattern presentElement = new Pattern(presentSikuliFile).similar(similarity);
            screen.find(presentElement); // This will throw an exception if not found
        } catch (FindFailed e) {
            Assertions.fail("Image not found on screen: " + sourceFileName);
        }
    }

    /**
     * Hover over the specified image.
     */
    public static void hoverOnImage(String sourceFileName, float similarity) {
        String sikuliFile = getSikuliFileByName(sourceFileName);

        Screen screen = new Screen();
        try {
            Pattern hoverElement = new Pattern(sikuliFile).similar(similarity);
            Match sourceMatch = screen.find(hoverElement);
            screen.hover(sourceMatch);
        } catch (FindFailed e) {
            e.printStackTrace();
            System.out.println("Failed to hover on image");
        }
    }

    /**
     * Load the specified image from resources, checking for supported extensions.
     */
    private static String getSikuliFileByName(String sourceFileName) {
        String[] extensions = {".png", ".jpg", ".jpeg"};
        InputStream input = null;

        // Attempt to locate the file with each extension
        for (String extension : extensions) {
            input = SikuliService.class.getClassLoader().getResourceAsStream(LOCAL_STORAGE_PATH + sourceFileName + extension);
            if (input != null) {
                sourceFileName += extension;
                break;
            }
        }

        if (input == null) {
            throw new RuntimeException("Image file not found with supported extensions (.png, .jpg, .jpeg): " + LOCAL_STORAGE_PATH + sourceFileName);
        }

        try {
            File tempFile = File.createTempFile("sikuli-", "-" + sourceFileName);
            tempFile.deleteOnExit();
            Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return tempFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load Sikuli image: " + sourceFileName);
        }
    }
}