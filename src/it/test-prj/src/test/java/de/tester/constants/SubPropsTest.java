package de.tester.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class SubPropsTest {

    @Test
    void testConstants() {
        assertEquals("One", SubProps.ONE);
        assertEquals("Two", SubProps.TWO);
        assertEquals("Three", SubProps.THREE);
    }

    @Test
    void testPropertiesFilenameName() {
        assertEquals("subdir/sub-props.properties", SubProps.getPropertiesFilename());
    }

    @Test
    void testPropertyLoad() throws IOException {
        final Properties props = SubProps.loadProperties();
        assertEquals("First", props.getProperty(SubProps.ONE));
        assertEquals("Second", props.getProperty(SubProps.TWO));
        assertEquals("Third", props.getProperty(SubProps.THREE));
    }

}
