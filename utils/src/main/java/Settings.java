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
import java.util.Iterator;

public class Settings {

    private Configuration config, defaultConfig;
    private final String location;
    private final String filename;
    private FileBasedConfigurationBuilder<FileBasedConfiguration> builder;

    private HashMap<String, HashMap<String, String>> settings;

    public Settings(String location, String filename) {

        this.location = location;
        this.filename = filename;

        defaultConfig = load("default.properties");

        Path dir = Paths.get(location + filename);

        if(!Files.exists(dir)) {
            try {
                Files.createFile(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else
            config = load(location + filename);

        loadSettings();
    }

    void setProp(String key, String value) {
        if (!config.containsKey(key))
            return;

        config.setProperty(key, value);
        settings.get(key).put(key, value);
        save();
    }

    void setPropList(String key, HashMap<String, String> value) {
        if (!config.containsKey(key))
            return;

        config.setProperty(key, value);
        settings.put(key, value);
        save();
    }

    /**
     * Get a setting from the config file that has a list of values
     * @param key
     * @return
     */
    public HashMap<String, String> getPropList(String key) {
        return settings.get(key);
    }

    /**
     * Get a setting from the config file
     * @param key
     * @return
     */
    public String getProp(String key) {
        return settings.get(key).get(key);
    }

    /**
     * Returns the default value for a setting
     * @param key
     * @return
     */
    private String getDefault(String key) {
        String value = defaultConfig.getString(key);

        switch (value) {
            case "default_location":
                return location;
        }

        return value;
    }

    /**
     * Loads the settings from the config file
     */
    private void loadSettings() {
        settings = new HashMap<>();

        // For each setting in the config file
        Iterator<String> keys = config.getKeys();
        while(keys.hasNext()) {

            // Get the key
            String key = keys.next();
            // Get the value
            String value = config.getString(key);

            HashMap<String, String> values = new HashMap<>();

            // Split the list of values (if it is a list)
            String[] props = value.split(";");
            // For each value in the list
            for (String prop : props) {
                // Split the key and value
                String[] pair = prop.split("=");

                // Prepare the key and value
                String newKey = key, newValue = pair[0];

                // If it's a couple, set the key and value
                if(pair.length > 1)
                {
                    newKey = pair[0];
                    // Check for default
                    if (pair[1].equals("default"))
                        newValue = getDefault(key+"."+pair[1]);
                    else
                        newValue = pair[1];
                }

                // Check for default
                if (newValue.equals("default"))
                    newValue = getDefault(key);

                // Add the key and value to the list
                values.put(newKey, newValue);
            }

            // Add the list to the settings
            settings.put(key, values);
        }
    }

    /**
     * Loads a config file
     * @param fileName Path to the file
     * @return
     */
    private Configuration load(String fileName) {
        Parameters params = new Parameters();

        builder =new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(params.properties()
                        .setFileName(fileName));
        try
        {
            return builder.getConfiguration();
        }
        catch(ConfigurationException cex)
        {
            cex.printStackTrace();
        }
        return null;
    }

    /**
     * Saves the config file
     */
    private void save() {
        try {
            builder.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
}
