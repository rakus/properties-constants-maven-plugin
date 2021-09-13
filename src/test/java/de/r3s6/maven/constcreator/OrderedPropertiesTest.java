package de.r3s6.maven.constcreator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class OrderedPropertiesTest {

    @Test
    void test() throws IOException {
        final Properties props = new OrderedProperties();
        try (final InputStream stream = OrderedPropertiesTest.class.getResourceAsStream("/test-props.properties")) {
            props.load(stream);
        }

        final Iterator<Object> iter = props.keySet().iterator();

        assertEquals("test0001", iter.next());
        assertEquals("test0002", iter.next());
        assertEquals("test0003", iter.next());
        assertEquals("test0004", iter.next());
        assertEquals("test0005", iter.next());
        assertEquals("test0006", iter.next());
        assertEquals("test0007", iter.next());
        assertEquals("test0008", iter.next());
        assertEquals("test0009", iter.next());
        assertEquals("test0010", iter.next());

    }

    @Test
    void testXml() throws IOException {
        final Properties props = new OrderedProperties();
        try (final InputStream stream = OrderedPropertiesTest.class.getResourceAsStream("/test-props.xml")) {
            props.loadFromXML(stream);
        }

        final Iterator<Object> iter = props.keySet().iterator();

        assertEquals("test0001", iter.next());
        assertEquals("test0002", iter.next());
        assertEquals("test0003", iter.next());
        assertEquals("test0004", iter.next());
        assertEquals("test0005", iter.next());
        assertEquals("test0006", iter.next());
        assertEquals("test0007", iter.next());
        assertEquals("test0008", iter.next());
        assertEquals("test0009", iter.next());
        assertEquals("test0010", iter.next());

    }

}
