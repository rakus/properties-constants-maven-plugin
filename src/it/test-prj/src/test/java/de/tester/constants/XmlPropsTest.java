package de.tester.constants;

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
        assertEquals("xml-props.xml", XmlProps.getPropertiesFilename());
    }

    @Test
    void testPropertyLoad() throws IOException {
        final Properties props = XmlProps.loadProperties();
        assertEquals("test0001 value", props.getProperty(XmlProps.TEST0001));
        assertEquals("test0002 value", props.getProperty(XmlProps.TEST0002));
        assertEquals("test0003 value", props.getProperty(XmlProps.TEST0003));
        assertEquals("test0004 value", props.getProperty(XmlProps.TEST0004));
        assertEquals("test0005 value", props.getProperty(XmlProps.TEST0005));
    }

}
