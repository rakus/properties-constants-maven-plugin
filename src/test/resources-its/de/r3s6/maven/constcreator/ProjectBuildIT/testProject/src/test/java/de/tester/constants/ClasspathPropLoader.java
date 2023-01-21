package de.tester.constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ClasspathPropLoader {

    public static Properties loadProperties(final String filename) throws IOException {
        final Properties properties = new Properties();
        try (InputStream stream = ClasspathPropLoader.class.getResourceAsStream("/" + filename)) {
            if (stream == null) {
                throw new IOException("Resource not found: /" + filename);
            }
            properties.load(stream);
        }
        return properties;
    }

    public static Properties loadXmlProperties(final String filename) throws IOException {
        final Properties properties = new Properties();
        try (InputStream stream = ClasspathPropLoader.class.getResourceAsStream("/" + filename)) {
            if (stream == null) {
                throw new IOException("Resource not found: /" + filename);
            }
            properties.loadFromXML(stream);
        }
        return properties;
    }

    private ClasspathPropLoader() {
        // nothing to instantiate
    }
}
