package de.tester.constants;
/*
 * Copyright 2021 Ralf Schandl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
