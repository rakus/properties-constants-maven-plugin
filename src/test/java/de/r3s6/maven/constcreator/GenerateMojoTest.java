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

import org.junit.jupiter.api.Test;

class GenerateMojoTest {

    @Test
    void testKey2Constant() {

        assertEquals("TEST_CASE", GenerateMojo.keyToConstant("test.case"));
        assertEquals("TEST__CASE", GenerateMojo.keyToConstant("test..Case"));
        assertEquals("TEST____CASE", GenerateMojo.keyToConstant("test....Case"));

        assertEquals("TEST_CASE", GenerateMojo.keyToConstant("test_case"));
        assertEquals("TEST____CASE", GenerateMojo.keyToConstant("test____case"));

        assertEquals("TEST_CASE", GenerateMojo.keyToConstant("test-case"));
        assertEquals("TEST__CASE", GenerateMojo.keyToConstant("test--case"));

        assertEquals("TEST___CASE", GenerateMojo.keyToConstant("test-._case"));

        assertEquals("TEST_CASE", GenerateMojo.keyToConstant("testCase"));
        assertEquals("TEST_CASE", GenerateMojo.keyToConstant("test Case"));
        assertEquals("TEST____CASE", GenerateMojo.keyToConstant("test    Case"));

        assertEquals("TEST____CASE", GenerateMojo.keyToConstant("   test    Case        "));

        assertEquals("TEST_CASE_MESSAGE", GenerateMojo.keyToConstant("test.case.message"));

        assertEquals("TEST$CASE", GenerateMojo.keyToConstant("test$case"));

        assertEquals("ÄÖÜ_ÄÖÜSS", GenerateMojo.keyToConstant("ÄÖÜ_äöüß"));

        assertEquals("_0ZERO", GenerateMojo.keyToConstant("0zero"));

        assertEquals("_PERCENT", GenerateMojo.keyToConstant("%percent"));

    }
}
