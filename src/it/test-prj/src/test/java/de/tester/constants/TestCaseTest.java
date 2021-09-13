package de.tester.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class TestCaseTest {

    @Test
    void testConstants() {
        assertEquals("umlauts", TestCase.UMLAUTS);
        assertEquals("0key", TestCase._0KEY);
        assertEquals("key-1", TestCase.KEY_1);
        assertEquals("key..2", TestCase.KEY__2);
        assertEquals("key$3", TestCase.KEY$3);
        assertEquals(".key4", TestCase._KEY4);
    }

    @Test
    void testPropertiesFilenameName() {
        assertEquals("test-case.properties", TestCase.getPropertiesFilename());
    }

    @Test
    void testPropertyLoad() throws IOException {
        final Properties props = TestCase.loadProperties();
        assertEquals("ÄÖÜäöüß", props.getProperty(TestCase.UMLAUTS));
        assertEquals("Key 1", props.getProperty(TestCase.KEY_1));
        assertEquals("Key 2", props.getProperty(TestCase.KEY__2));
        assertEquals("Key 3", props.getProperty(TestCase.KEY$3));
        assertEquals("Key 4", props.getProperty(TestCase._KEY4));
    }

}
