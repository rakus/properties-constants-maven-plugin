package de.r3s6.maven.constcreator;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertEquals("_0ZERO", ConstantsCreator.keyToConstant("0zero"));

    }

}
