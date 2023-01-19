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

class SubPropsTest {

    @Test
    void testConstants() {
        assertEquals("One", SubProps.ONE);
        assertEquals("Two", SubProps.TWO);
        assertEquals("Three", SubProps.THREE);
    }

    @Test
    void testPropertiesFilenameName() {
        assertEquals("subdir/sub-props.properties", SubProps.getPropertiesFilename());
    }

    @Test
    void testPropertyLoad() throws IOException {
        final Properties props = SubProps.loadProperties();
        assertEquals("First", props.getProperty(SubProps.ONE));
        assertEquals("Second", props.getProperty(SubProps.TWO));
        assertEquals("Third", props.getProperty(SubProps.THREE));
    }

}
