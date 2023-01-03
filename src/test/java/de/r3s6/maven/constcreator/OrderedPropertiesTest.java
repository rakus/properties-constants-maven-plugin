package de.r3s6.maven.constcreator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
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

        final Enumeration<Object> enumeration = props.keys();

        assertEquals("test0001", enumeration.nextElement());
        assertEquals("test0002", enumeration.nextElement());
        assertEquals("test0003", enumeration.nextElement());
        assertEquals("test0004", enumeration.nextElement());
        assertEquals("test0005", enumeration.nextElement());
        assertEquals("test0006", enumeration.nextElement());
        assertEquals("test0007", enumeration.nextElement());
        assertEquals("test0008", enumeration.nextElement());
        assertEquals("test0009", enumeration.nextElement());
        assertEquals("test0010", enumeration.nextElement());
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

        final Enumeration<Object> enumeration = props.keys();

        assertEquals("test0001", enumeration.nextElement());
        assertEquals("test0002", enumeration.nextElement());
        assertEquals("test0003", enumeration.nextElement());
        assertEquals("test0004", enumeration.nextElement());
        assertEquals("test0005", enumeration.nextElement());
        assertEquals("test0006", enumeration.nextElement());
        assertEquals("test0007", enumeration.nextElement());
        assertEquals("test0008", enumeration.nextElement());
        assertEquals("test0009", enumeration.nextElement());
        assertEquals("test0010", enumeration.nextElement());
    }

    @Test
    void testRemove() throws IOException {
        final Properties props = new OrderedProperties();
        try (final InputStream stream = OrderedPropertiesTest.class.getResourceAsStream("/test-props.properties")) {
            props.load(stream);
        }

        props.remove("test00010");

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

    }

    @Test
    void testRemoveWithValue() throws IOException {
        final Properties props = new OrderedProperties();
        try (final InputStream stream = OrderedPropertiesTest.class.getResourceAsStream("/test-props.properties")) {
            props.load(stream);
        }

        props.remove("test0001", "Value0001");

        // Will not remove as value doesn't match
        props.remove("test0002", "InvalidValue");

        final Iterator<Object> iter = props.keySet().iterator();

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
    void testClear() throws IOException {
        final Properties props = new OrderedProperties();
        try (final InputStream stream = OrderedPropertiesTest.class.getResourceAsStream("/test-props.properties")) {
            props.load(stream);
        }
        assertEquals(10, props.keySet().size());

        props.clear();

        assertTrue(props.keySet().isEmpty());
        assertFalse(props.keys().hasMoreElements());
    }

    @Test
    void testEntrySetNotImplemented() throws IOException {
        final Properties props = new OrderedProperties();

        assertThrows(UnsupportedOperationException.class, () -> props.entrySet());
    }
}
