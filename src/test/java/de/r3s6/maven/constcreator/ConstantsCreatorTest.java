package de.r3s6.maven.constcreator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConstantsCreatorTest {

    @Test
    void testKey2Constant() {

        assertEquals("TEST_CASE", ConstantsCreator.keyToConstant("test.case"));
        assertEquals("TEST__CASE", ConstantsCreator.keyToConstant("test..Case"));
        assertEquals("TEST____CASE", ConstantsCreator.keyToConstant("test....Case"));

        assertEquals("TEST_CASE", ConstantsCreator.keyToConstant("test_case"));
        assertEquals("TEST____CASE", ConstantsCreator.keyToConstant("test____case"));

        assertEquals("TEST_CASE", ConstantsCreator.keyToConstant("test-case"));
        assertEquals("TEST__CASE", ConstantsCreator.keyToConstant("test--case"));

        assertEquals("TEST___CASE", ConstantsCreator.keyToConstant("test-._case"));

        assertEquals("TEST_CASE", ConstantsCreator.keyToConstant("testCase"));
        assertEquals("TEST_CASE", ConstantsCreator.keyToConstant("test Case"));
        assertEquals("TEST____CASE", ConstantsCreator.keyToConstant("test    Case"));

        assertEquals("TEST____CASE", ConstantsCreator.keyToConstant("   test    Case        "));

        assertEquals("TEST_CASE_MESSAGE", ConstantsCreator.keyToConstant("test.case.message"));

        assertEquals("TEST$CASE", ConstantsCreator.keyToConstant("test$case"));

        assertEquals("ÄÖÜ_ÄÖÜSS", ConstantsCreator.keyToConstant("ÄÖÜ_äöüß"));

        assertEquals("_0ZERO", ConstantsCreator.keyToConstant("0zero"));

        assertEquals("_PERCENT", ConstantsCreator.keyToConstant("%percent"));

    }

    @Test
    void testIsValidPackageName() {
        assertTrue(ConstantsCreator.isValidPackageName("de"));
        assertTrue(ConstantsCreator.isValidPackageName("de.r3s6"));
        assertTrue(ConstantsCreator.isValidPackageName("de.r3s6.maven"));
        assertTrue(ConstantsCreator.isValidPackageName("de.r3s6.maven.constcreator"));
        assertTrue(ConstantsCreator.isValidPackageName("test.test_case.hello"));

        assertFalse(ConstantsCreator.isValidPackageName(null));
        assertFalse(ConstantsCreator.isValidPackageName(""));
        assertFalse(ConstantsCreator.isValidPackageName("  "));
        assertFalse(ConstantsCreator.isValidPackageName(" de "));
        assertFalse(ConstantsCreator.isValidPackageName("de,r3s6"));
        assertFalse(ConstantsCreator.isValidPackageName("de.r3s6.false"));
        assertFalse(ConstantsCreator.isValidPackageName("de.r3s6.assert"));
        assertFalse(ConstantsCreator.isValidPackageName("de.r3-s6"));
        assertFalse(ConstantsCreator.isValidPackageName("de..r3s6"));
        assertFalse(ConstantsCreator.isValidPackageName("de. .r3s6"));
        assertFalse(ConstantsCreator.isValidPackageName("de .r3s6"));
    }

}
