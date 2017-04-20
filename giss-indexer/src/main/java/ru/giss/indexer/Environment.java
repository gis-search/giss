package ru.giss.indexer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author Ruslan Izmaylov
 */
public class Environment {

    private static Properties config;

    public static Properties getConfig() throws IOException {
        if (config != null) return config;
        URL confResource = ClassLoader.getSystemResource("core.properties");
        if (confResource == null) {
            throw new FileNotFoundException("Can't find core.properties file");
        }
        config = new Properties();
        try (InputStream is = confResource.openStream()) {
            config.load(is);
        }
        return config;
    }
}
