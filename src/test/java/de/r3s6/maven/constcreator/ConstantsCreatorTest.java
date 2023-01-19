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
}
