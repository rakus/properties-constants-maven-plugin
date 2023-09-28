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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

class GeneratorRequestTest {

    @Test
    @SuppressWarnings("java:S5785")
    void testHashCodeEquals() {

        GeneratorRequest.Builder b = new GeneratorRequest.Builder();
        b.className("a.b.JavaClass");

        GeneratorRequest gr1 = b.build();

        b.javaFileName("a/b/JavaClass.java");
        b.javaFile(new File("a/b/JavaClass.java"));
        b.propertiesFileName("a/b/javaClass.properties");
        b.propertiesFile(new File("a/b/javaClass.properties"));
        b.xmlProperties(false);
        b.bundleName("a.b.javaClass");

        GeneratorRequest gr2 = b.build();

        assertEquals(gr1.hashCode(), gr2.hashCode());

        assertTrue(gr1.equals(gr2));
        assertTrue(gr2.equals(gr1));
        assertTrue(gr1.equals(gr1));
    }

    @Test
    @SuppressWarnings("java:S5785")
    void testNotEquals() {

        GeneratorRequest.Builder b = new GeneratorRequest.Builder();
        b.className("a.b.JavaClass");
        b.javaFileName("a/b/JavaClass.java");
        b.javaFile(new File("a/b/JavaClass.java"));
        b.propertiesFileName("a/b/javaClass.properties");
        b.propertiesFile(new File("a/b/javaClass.properties"));
        b.xmlProperties(false);
        b.bundleName("a.b.javaClass");

        GeneratorRequest gr1 = b.build();

        b.className("a.b.JavaClass2");
        GeneratorRequest gr2 = b.build();

        assertFalse(gr1.equals(gr2));
        assertFalse(gr2.equals(gr1));
    }

    @Test
    @SuppressWarnings({ "unlikely-arg-type", "java:S5785" })
    void testEqualsFalse() {
        GeneratorRequest.Builder b = new GeneratorRequest.Builder();
        b.className("a.b.Class");

        GeneratorRequest gr1 = b.build();

        assertFalse(gr1.equals(null));
        assertFalse(gr1.equals("Not Me"));
    }

}
