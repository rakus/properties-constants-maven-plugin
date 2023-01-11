package de.r3s6.maven.constcreator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PropEntryTest {

    @Test
    void testSimple() {
        PropEntry entry = new PropEntry("first.name", "Joe");
        assertEquals("first.name", entry.getKey());
        assertEquals("Joe", entry.getValue());

        assertEquals("FIRST_NAME", entry.getConstantName());
        assertEquals("firstName", entry.getVariableName());
        assertEquals("getFirstName", entry.getGetterName());
    }

    @Test
    void testInvalidStartChar() {
        PropEntry entry = new PropEntry("0entry", "nothing");

        assertEquals("_0ENTRY", entry.getConstantName());
        assertEquals("_0entry", entry.getVariableName());
        assertEquals("get0entry", entry.getGetterName());
    }

    @Test
    void testInvalidStartChar2() {
        PropEntry entry = new PropEntry("%entry", "nothing");

        assertEquals("ENTRY", entry.getConstantName());
        assertEquals("entry", entry.getVariableName());
        assertEquals("getEntry", entry.getGetterName());
    }

    @Test
    void testInvalidChar() {
        PropEntry entry = new PropEntry("test%entry", "nothing");

        assertEquals("TEST_ENTRY", entry.getConstantName());
        assertEquals("testEntry", entry.getVariableName());
        assertEquals("getTestEntry", entry.getGetterName());
    }

    @Test
    void testUtf8Char() {
        PropEntry entry = new PropEntry("test.äöüß", "nothing");

        assertEquals("TEST_ÄÖÜSS", entry.getConstantName());
        assertEquals("testÄöüß", entry.getVariableName());
        assertEquals("getTestÄöüß", entry.getGetterName());
    }

    @Test
    void testTrailingSeparator() {
        PropEntry entry = new PropEntry("test.case%", "nothing");

        assertEquals("TEST_CASE", entry.getConstantName());
        assertEquals("testCase", entry.getVariableName());
        assertEquals("getTestCase", entry.getGetterName());
    }

    @Test
    void testUnderScore() {
        PropEntry entry = new PropEntry("test_case", "one");

        assertEquals("TEST_CASE", entry.getConstantName());
        assertEquals("testCase", entry.getVariableName());
        assertEquals("getTestCase", entry.getGetterName());
    }

    @Test
    void testMultiDivider() {
        PropEntry entry = new PropEntry("test..case", "one");

        assertEquals("TEST_CASE", entry.getConstantName());
        assertEquals("testCase", entry.getVariableName());
        assertEquals("getTestCase", entry.getGetterName());
    }

}
