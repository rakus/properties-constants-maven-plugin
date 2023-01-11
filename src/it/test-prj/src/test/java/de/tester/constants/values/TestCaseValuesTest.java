package de.tester.constants.values;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class TestCaseValuesTest {

    @Test
    void testConstants() {
        assertEquals("ÄÖÜäöüß", TestCase.UMLAUTS);
        assertEquals("Key 0", TestCase._0KEY);
        assertEquals("Key 1", TestCase.KEY_1);
        assertEquals("Key 2", TestCase.KEY_2);
        assertEquals("Key 3", TestCase.KEY$3);
        assertEquals("Key 4", TestCase.KEY4);
    }

}
