package de.r3s6.maven.constcreator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GeneratorRequestTest {

    @Test
    void testSimple() {

        GeneratorRequest gr = new GeneratorRequest("pkg", "test.properties", false);
        assertEquals("test.properties", gr.getPropertiesFileName());
        assertEquals("test", gr.getPropertiesBasename());
        assertEquals("pkg.Test", gr.getFullClassName());
        assertEquals("pkg/Test.java", gr.getJavaFileName());

        gr = new GeneratorRequest("pkg", "test-case.properties", false);
        assertEquals("test-case.properties", gr.getPropertiesFileName());
        assertEquals("test-case", gr.getPropertiesBasename());
        assertEquals("pkg.TestCase", gr.getFullClassName());
        assertEquals("pkg/TestCase.java", gr.getJavaFileName());

    }

    @Test
    void testSubdir() {

        final GeneratorRequest gr = new GeneratorRequest("pkg", "test/test.properties", false);
        assertEquals("test/test.properties", gr.getPropertiesFileName());
        assertEquals("test/test", gr.getPropertiesBasename());
        assertEquals("pkg.test.Test", gr.getFullClassName());
        assertEquals("pkg/test/Test.java", gr.getJavaFileName());
    }

    @Test
    void testSubdirFlatten() {

        final GeneratorRequest gr = new GeneratorRequest("pkg", "test/test.properties", true);
        assertEquals("test/test.properties", gr.getPropertiesFileName());
        assertEquals("test/test", gr.getPropertiesBasename());
        assertEquals("pkg.Test", gr.getFullClassName());
        assertEquals("pkg/Test.java", gr.getJavaFileName());
    }

    @Test
    void testEquals() {

        final GeneratorRequest gr = new GeneratorRequest("pkg", "test/test.properties", true);
        assertEquals("test/test.properties", gr.getPropertiesFileName());
        assertEquals("test/test", gr.getPropertiesBasename());
        assertEquals("pkg.Test", gr.getFullClassName());
        assertEquals("pkg/Test.java", gr.getJavaFileName());

        final GeneratorRequest gr2 = new GeneratorRequest("pkg", "test/sub/test.properties", true);
        assertEquals("test/sub/test.properties", gr2.getPropertiesFileName());
        assertEquals("test/sub/test", gr2.getPropertiesBasename());
        assertEquals("pkg.Test", gr2.getFullClassName());
        assertEquals("pkg/Test.java", gr2.getJavaFileName());

        assertTrue(gr.equals(gr2));
        assertTrue(gr2.equals(gr));
        assertEquals(gr.hashCode(), gr2.hashCode());

    }

    @Test
    void testLocale() {

        GeneratorRequest gr = new GeneratorRequest("pkg", "test/test_en.properties", false);
        assertEquals("test/test_en.properties", gr.getPropertiesFileName());
        assertEquals("test/test", gr.getPropertiesBasename());
        assertEquals("pkg.test.Test", gr.getFullClassName());
        assertEquals("pkg/test/Test.java", gr.getJavaFileName());

        gr = new GeneratorRequest("pkg", "test/test_en_US.properties", false);
        assertEquals("test/test_en_US.properties", gr.getPropertiesFileName());
        assertEquals("test/test", gr.getPropertiesBasename());
        assertEquals("pkg.test.Test", gr.getFullClassName());
        assertEquals("pkg/test/Test.java", gr.getJavaFileName());
    }

    @Test
    void testXml() {

        final GeneratorRequest gr = new GeneratorRequest("pkg", "test/test.xml", true);
        assertEquals("test/test.xml", gr.getPropertiesFileName());
        assertEquals("test/test", gr.getPropertiesBasename());
        assertEquals("pkg.Test", gr.getFullClassName());
        assertEquals("pkg/Test.java", gr.getJavaFileName());
    }

}
