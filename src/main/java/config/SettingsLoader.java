package config;

import exception.AppException;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

public class SettingsLoader {

    private Properties properties;

    public SettingsLoader() {

    }

    public void load(Path catalogLocation) throws AppException {
        properties = new Properties();
        InputStream stream;
        try {
            stream = new FileInputStream(new File(catalogLocation.toString() + "\\settings.properties"));
            properties.load(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new AppException("[ERROR] Can't find settings file");
        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException("[ERROR] Can't load settings");
        }
        System.out.println(properties.getProperty("test"));
    }

    public Properties getProperties() {
        return properties;
    }
}
