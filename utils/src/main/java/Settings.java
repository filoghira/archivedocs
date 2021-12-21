import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Settings {

    private Configuration config;
    private final String location;
    private final String filename;
    private FileBasedConfigurationBuilder<FileBasedConfiguration> builder;

    public Settings(String location, String filename) {

        this.location = location;
        this.filename = filename;

        Path dir = Paths.get(location + filename);

        if(!Files.exists(dir)) {
            try {
                Files.createFile(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            template();
        }else
            load();

    }

    public void setDatabaseLocation(String location) {
        Path dir = Paths.get(location);

        if(!Files.isDirectory(dir))
            throw new IllegalArgumentException("Invalid directory");

        config.setProperty("database.location", location);

        save();
    }

    public void setWindowSize(int width, int height) {

        // Check if the window size is valid
        if(width < 600 || height < 400)
            throw new IllegalArgumentException("Invalid window size");

        config.setProperty("width", width);
        config.setProperty("height", height);

        save();
    }

    public String getDatabaseLocation() {
        String location = config.getProperty("database.location").toString();

        if(location.equals("default"))
            location = this.location;

        Path dir = Paths.get(location);

        if(!Files.isDirectory(dir))
            throw new IllegalArgumentException("Invalid directory");

        return location;
    }

    public String getDatabaseStorageLocation() {
        String location = config.getProperty("database.storage.location").toString();

        if(location.equals("default"))
            location = this.location + "docs";

        Path dir = Paths.get(location);

        return location;
    }

    public int[] getWindowSize() {
        int width = Integer.parseInt(config.getProperty("width").toString());
        int height = Integer.parseInt(config.getProperty("height").toString());

        // Check if the window size is valid
        if(width < 600 || height < 400)
            throw new IllegalArgumentException("Invalid window size");

        return new int[] {width, height};
    }

    public String getLocation() {
        return location;
    }

    public String getFileIconsLocation() {
        String temp = config.getProperty("file.icons.location").toString();

        if (temp.equals("default"))
            temp = "icons";
        return location + temp;
    }

    private void load() {
        Parameters params = new Parameters();

        builder =new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(params.properties()
                        .setFileName(location + filename));
        try
        {
            config = builder.getConfiguration();
        }
        catch(ConfigurationException cex)
        {
            cex.printStackTrace();
        }
    }

    private void template() {
        load();

        config.setProperty("database.location", "default");
        config.setProperty("database.storage.location", "default");
        config.setProperty("width", 800);
        config.setProperty("height", 600);
        config.setProperty("file.icons.location", "default");
        config.setProperty("file.icons.extensions", "default");

        save();
    }

    private void save() {
        try {
            builder.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getIconLinks() {
        String[] links =  config.getStringArray("file.icons.links");
        // Extract each link and create a hashmap
        HashMap<String, String> icons = new HashMap<>();
        for (String link : links) {
            String[] temp = link.split(";");
            icons.put(temp[0], temp[1]);
        }

        return icons;
    }
}
