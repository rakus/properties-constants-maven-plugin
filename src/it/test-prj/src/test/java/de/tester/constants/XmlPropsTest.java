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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class XmlPropsTest {

    @Test
    void testConstants() {
        assertEquals("test0001", XmlProps.TEST0001);
        assertEquals("test0002", XmlProps.TEST0002);
        assertEquals("test0003", XmlProps.TEST0003);
        assertEquals("test0004", XmlProps.TEST0004);
        assertEquals("test0005", XmlProps.TEST0005);
    }

    @Test
    void testPropertiesFilenameName() {
        assertEquals("xml-props.xml", XmlProps.PROPERTIES_FILE_NAME);
    }

    @Test
    void testPropertyLoad() throws IOException {
        final Properties props = ClasspathPropLoader.loadXmlProperties(XmlProps.PROPERTIES_FILE_NAME);
        assertEquals("test0001 value", props.getProperty(XmlProps.TEST0001));
        assertEquals("test0002 value", props.getProperty(XmlProps.TEST0002));
        assertEquals("test0003 value", props.getProperty(XmlProps.TEST0003));
        assertEquals("test0004 value", props.getProperty(XmlProps.TEST0004));
        assertEquals("test0005 value", props.getProperty(XmlProps.TEST0005));
    }

}
