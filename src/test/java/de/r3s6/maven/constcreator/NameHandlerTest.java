package de.r3s6.maven.constcreator;
/*
 * Copyright 2023 Ralf Schandl
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import de.r3s6.maven.constcreator.NameHandler.JavaNames;

class NameHandlerTest {

    @ParameterizedTest
    @CsvSource({
        "test_case, TEST_CASE, testCase, getTestCase",
        "test.case, TEST_CASE, testCase, getTestCase",
        "test..case, TEST_CASE, testCase, getTestCase",
        "test%case, TEST_CASE, testCase, getTestCase",
        "test%%case, TEST_CASE, testCase, getTestCase",
        "test%%%case, TEST_CASE, testCase, getTestCase",
        "test%.%case, TEST_CASE, testCase, getTestCase",
        "_test_case_, TEST_CASE, testCase, getTestCase",
        "__test_case__, TEST_CASE, testCase, getTestCase",
        "%test_case%, TEST_CASE, testCase, getTestCase",
        "%%test_case%%, TEST_CASE, testCase, getTestCase",
        "testCase, TEST_CASE, testCase, getTestCase",
        "test$case, TEST$CASE, test$case, getTest$case",
        "%test, TEST, test, getTest",
        "0test, _0TEST, _0test, get0test",
        "test.HTTP.request, TEST_HTTP_REQUEST, testHttpRequest, getTestHttpRequest",
        "..., ___, ___, get___",
        "%%%, ___, ___, get___",
        "X, X, x, getX",
        "x, X, x, getX",
        "info@example.nix, INFO_EXAMPLE_NIX, infoExampleNix, getInfoExampleNix",
        })
    void tests(final String key, final String constantName, final String variableName, final String getterName) {
        JavaNames names = NameHandler.createJavaNames(key);
        assertEquals(constantName, names.getConstantName());
        assertEquals(variableName, names.getVariableName());
        assertEquals(getterName, names.getGetterName());
    }

    @ParameterizedTest
    @CsvSource({
        "t, T",
        "test, Test",
        "%test, Test",
        "%%test, Test",
        "%%%test, Test",
        "%.%test, Test",
        "%test%, Test",
        "%%test%%, Test",
        "%%%test%%%, Test",
        "0test, _0test",
        "test_case, TestCase",
        "test.case.result, TestCaseResult",
        "h.t.t.p, HTTP",
        "info@example.nix, InfoExampleNix",
    })
    void testBuildTypeName(final String key, final String typeName) {
        assertEquals(typeName, NameHandler.createTypeName(key));
    }

    @Test
    void testEmptyName() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> NameHandler.createJavaNames(""));
        assertEquals("name must not be empty", ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () -> NameHandler.createTypeName(""));
        assertEquals("name must not be empty", ex.getMessage());
    }

    @Test
    void testNameNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> NameHandler.createJavaNames(null));
        assertEquals("name must not be null", ex.getMessage());

        ex = assertThrows(NullPointerException.class, () -> NameHandler.createTypeName(null));
        assertEquals("name must not be null", ex.getMessage());
    }

}
