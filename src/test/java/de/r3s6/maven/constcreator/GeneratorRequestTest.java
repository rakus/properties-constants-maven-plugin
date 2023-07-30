package de.r3s6.maven.constcreator;

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
