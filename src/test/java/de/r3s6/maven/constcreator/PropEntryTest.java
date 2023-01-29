package de.r3s6.maven.constcreator;
/*
 * Copyright 2021 Ralf Schandl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PropEntryTest {

    @Test
    void testIntroExamples() {
        assertEquals("TEST_CASE", new PropEntry("testCase", "value").getConstantName());
        assertEquals("TEST_CASE", new PropEntry("test.case", "value").getConstantName());
        assertEquals("TEST_CASE", new PropEntry("test..case", "value").getConstantName());
        assertEquals("TEST_CASE", new PropEntry("test_case", "value").getConstantName());
        assertEquals("TEST_CASE", new PropEntry("test%case", "value").getConstantName());
        assertEquals("TEST$CASE", new PropEntry("test$case", "value").getConstantName());
        assertEquals("_0TEST", new PropEntry("0test", "value").getConstantName());
        assertEquals("TEST", new PropEntry("%test", "value").getConstantName());
    }

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

    @Test
    void testEmptyKeyException() {
       IllegalArgumentException thr =  assertThrows(IllegalArgumentException.class, () -> new PropEntry("", "one"));

       assertEquals("empty key", thr.getMessage());
    }

    @Test
    void testNullKeyException() {
       NullPointerException thr =  assertThrows(NullPointerException.class, () -> new PropEntry(null, "one"));
       assertEquals("PropEntry.key must not be null", thr.getMessage());
    }
}
