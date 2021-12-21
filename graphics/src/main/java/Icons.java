import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Icons {

    private final HashMap<String, Image> icons;

    public Icons(String iconPath, HashMap<String, String> iconLinks) {
        icons = new HashMap<>();
        init(iconPath, iconLinks);
    }

    public Image getIcon(final String extension) {
        if (icons.containsKey(extension))
            return icons.get(extension);
        else
            return icons.get("default");
    }

    void init(String iconPath, HashMap<String, String> links) {
        File directory = new File(iconPath);
        if (! directory.exists()) {
            // Create the directory
            if (!directory.mkdir())
                throw new IllegalArgumentException("Could not create directory: " + iconPath);
        }

        // Given the icons' links, update the files
        for (String key : links.keySet()) {
            String value = links.get(key);
            String path = iconPath + "\\"+ key + ".png";
            try {
                Files.copy(new URL(value).openStream(),Paths.get(path));
            }catch (FileAlreadyExistsException e) {
                // Do nothing
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                icons.put(key, new Image(path, 16, 16, true, true));
            }
        }
    }
}
