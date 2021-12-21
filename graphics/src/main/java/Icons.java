import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Icons {

    private final HashMap<String, Image> icons;

    public Icons(String iconPath) {

        File directory = new File(iconPath);
        if (! directory.exists())
            if(!directory.mkdir())
                throw new IllegalArgumentException("Could not create directory: " + iconPath);

        icons = new HashMap<>();

        final File folder = new File(iconPath);

        try {
            loadIcons(folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadIcons(final File folder) throws IOException {
        File[] files = folder.listFiles();

        if(files == null)
            return;

        for (final File fileEntry : files) {
            String filename = fileEntry.getName();
            icons.put(filename.substring(0, filename.lastIndexOf(".")), new Image(fileEntry.getAbsolutePath(), 16, 16, true, true));
        }
    }

    public Image getIcon(final String extension) {
        return icons.get(extension);
    }
}
