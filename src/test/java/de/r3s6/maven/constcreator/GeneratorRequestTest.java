package de.r3s6.maven.constcreator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

class GeneratorRequestTest {

    private static File IN = new File("input-dir");
    private static File OUT = new File("output-dir");

    @Test
    void testSimple() {

        GeneratorRequest gr = new GeneratorRequest(IN, OUT, "pkg", "test.properties", false, "");
        assertEquals("test.properties", gr.getPropertiesFileName());
        assertEquals("test", gr.getBundleName());
        assertEquals("pkg.Test", gr.getFullClassName());
        assertEquals("pkg/Test.java", gr.getJavaFileName());
        assertEquals("input-dir/test.properties", gr.getPropertiesFile().getPath());
        assertEquals("output-dir/pkg/Test.java", gr.getJavaFile().getPath());

        gr = new GeneratorRequest(IN, OUT, "pkg", "test-case.properties", false, "");
        assertEquals("test-case.properties", gr.getPropertiesFileName());
        assertEquals("test-case", gr.getBundleName());
        assertEquals("pkg.TestCase", gr.getFullClassName());
        assertEquals("pkg/TestCase.java", gr.getJavaFileName());
        assertEquals("input-dir/test-case.properties", gr.getPropertiesFile().getPath());
        assertEquals("output-dir/pkg/TestCase.java", gr.getJavaFile().getPath());

    }

    @Test
    void testSubdir() {

        final GeneratorRequest gr = new GeneratorRequest(IN, OUT, "pkg", "test/test.properties", false, "");
        assertEquals("test/test.properties", gr.getPropertiesFileName());
        assertEquals("test/test", gr.getBundleName());
        assertEquals("pkg.test.Test", gr.getFullClassName());
        assertEquals("pkg/test/Test.java", gr.getJavaFileName());
        assertEquals("input-dir/test/test.properties", gr.getPropertiesFile().getPath());
        assertEquals("output-dir/pkg/test/Test.java", gr.getJavaFile().getPath());
    }

    @Test
    void testSubdirFlatten() {

        final GeneratorRequest gr = new GeneratorRequest(IN, OUT, "pkg", "test/test.properties", true, "");
        assertEquals("test/test.properties", gr.getPropertiesFileName());
        assertEquals("test/test", gr.getBundleName());
        assertEquals("pkg.Test", gr.getFullClassName());
        assertEquals("pkg/Test.java", gr.getJavaFileName());
        assertEquals("input-dir/test/test.properties", gr.getPropertiesFile().getPath());
        assertEquals("output-dir/pkg/Test.java", gr.getJavaFile().getPath());
    }

    @Test
    @SuppressWarnings({ "unlikely-arg-type", "java:S5785" })
    void testEquals() {

        final GeneratorRequest gr = new GeneratorRequest(IN, OUT, "pkg", "test/test.properties", true, "");
        assertEquals("pkg.Test", gr.getFullClassName());

        final GeneratorRequest gr2 = new GeneratorRequest(IN, OUT, "pkg", "test/sub/test.properties", true, "");
        assertEquals("pkg.Test", gr2.getFullClassName());

        // testing equals() here, so don't use assertEquals(), but call equal() directly

        assertTrue(gr.equals(gr));
        assertTrue(gr.equals(gr2));
        assertTrue(gr2.equals(gr));

        assertFalse(gr.equals(null));
        assertFalse(gr.equals("pkg.Test"));

        assertEquals(gr.hashCode(), gr2.hashCode());

    }

    @Test
    void testLocale() {

        GeneratorRequest gr = new GeneratorRequest(IN, OUT, "pkg", "test/test_en.properties", false, "");
        assertEquals("test/test_en.properties", gr.getPropertiesFileName());
        assertEquals("test/test", gr.getBundleName());
        assertEquals("pkg.test.Test", gr.getFullClassName());
        assertEquals("pkg/test/Test.java", gr.getJavaFileName());
        assertEquals("input-dir/test/test_en.properties", gr.getPropertiesFile().getPath());
        assertEquals("output-dir/pkg/test/Test.java", gr.getJavaFile().getPath());

        gr = new GeneratorRequest(IN, OUT, "pkg", "test/test_en_US.properties", false, "");
        assertEquals("test/test_en_US.properties", gr.getPropertiesFileName());
        assertEquals("test/test", gr.getBundleName());
        assertEquals("pkg.test.Test", gr.getFullClassName());
        assertEquals("pkg/test/Test.java", gr.getJavaFileName());
        assertEquals("input-dir/test/test_en_US.properties", gr.getPropertiesFile().getPath());
        assertEquals("output-dir/pkg/test/Test.java", gr.getJavaFile().getPath());
    }

    @Test
    void testXml() {

        final GeneratorRequest gr = new GeneratorRequest(IN, OUT, "pkg", "test/test.xml", false, "");
        assertEquals("test/test.xml", gr.getPropertiesFileName());
        assertEquals("test/test", gr.getBundleName());
        assertEquals("pkg.test.Test", gr.getFullClassName());
        assertEquals("pkg/test/Test.java", gr.getJavaFileName());
        assertEquals("input-dir/test/test.xml", gr.getPropertiesFile().getPath());
        assertEquals("output-dir/pkg/test/Test.java", gr.getJavaFile().getPath());
    }

    @Test
    void testInvalidConstruction() {

        assertThrows(NullPointerException.class,
                () -> new GeneratorRequest(null, OUT, "pkg", "test/test.xml", false, ""));
        assertThrows(NullPointerException.class,
                () -> new GeneratorRequest(IN, null, "pkg", "test/test.xml", false, ""));
        assertThrows(NullPointerException.class, () -> new GeneratorRequest(IN, OUT, null, "test/test.xml", false, ""));
        assertThrows(NullPointerException.class, () -> new GeneratorRequest(IN, OUT, "pkg", null, false, ""));
        assertThrows(NullPointerException.class,
                () -> new GeneratorRequest(IN, OUT, "pkg", "test/test.xml", false, null));
        assertThrows(IllegalArgumentException.class,
                () -> new GeneratorRequest(IN, OUT, "", "test/test.xml", false, ""));
        assertThrows(IllegalArgumentException.class,
                () -> new GeneratorRequest(IN, OUT, "   ", "test/test.xml", false, ""));

    }

}
