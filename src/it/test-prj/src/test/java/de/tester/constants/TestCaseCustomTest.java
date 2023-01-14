package de.tester.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class TestCaseCustomTest {

    @Test
    void testConstants() {
        assertEquals("umlauts", TestCaseCustom.UMLAUTS_KEY);
        assertEquals("0key", TestCaseCustom._0KEY_KEY);

        assertEquals("ÄÖÜäöüß", TestCaseCustom.UMLAUTS_VALUE);
        assertEquals("Key 0", TestCaseCustom._0KEY_VALUE);
    }
}
