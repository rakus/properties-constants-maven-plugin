package de.tester.constants;
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

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class TestCaseTest {

    @Test
    void testConstants() {
        assertEquals("umlauts", TestCase.UMLAUTS);
        assertEquals("0key", TestCase._0KEY);
        assertEquals("key-1", TestCase.KEY_1);
        assertEquals("key..2", TestCase.KEY_2);
        assertEquals("key$3", TestCase.KEY$3);
        assertEquals(".key4", TestCase.KEY4);
    }

    @Test
    void testPropertiesFilenameName() {
        assertEquals("test-case.properties", TestCase.getPropertiesFilename());
    }

    @Test
    void testPropertyLoad() throws IOException {
        final Properties props = TestCase.loadProperties();
        assertEquals("ÄÖÜäöüß", props.getProperty(TestCase.UMLAUTS));
        assertEquals("Key 1", props.getProperty(TestCase.KEY_1));
        assertEquals("Key 2", props.getProperty(TestCase.KEY_2));
        assertEquals("Key 3", props.getProperty(TestCase.KEY$3));
        assertEquals("Key 4", props.getProperty(TestCase.KEY4));
    }

}
