package de.r3s6.maven.constcreator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import freemarker.template.TemplateException;

class TemplateHandlerTest {

    private static final String UTF8 = "UTF-8";

    private static Map<String, String> model;

    @BeforeAll
    static void beforeAll() {
        Map<String, String> m = new HashMap<>();
        m.put("firstName", "Luke");
        m.put("lastName", "Skywalker");
        model = Collections.unmodifiableMap(m);
    }

    @Test
    void testFromClasspath() throws IOException, TemplateException {
        File basedir =  new File(".");

        TemplateHandler th = new TemplateHandler(basedir, UTF8);

        StringOutputStream os = new StringOutputStream();
        PrintWriter pw = new PrintWriter(os);

        th.process("test-basedir/custom-template.ftl", model, pw);
        assertEquals("Luke Skywalker", os.toString());
        os.reset();
    }

    @Test
    void testFromBasedir() throws IOException, TemplateException {
        File basedir =  new File("src/test/resources/test-basedir");

        TemplateHandler th = new TemplateHandler(basedir, UTF8);

        StringOutputStream os = new StringOutputStream();
        PrintWriter pw = new PrintWriter(os);

        th.process("custom-template.ftl", model, pw);
        assertEquals("Luke Skywalker", os.toString());
        os.reset();
    }

    @Test
    void testInvalidBasedir() throws IOException, TemplateException {
        File basedir =  new File("dir-does-not-exist");
        IllegalStateException thr = assertThrows(IllegalStateException.class, () -> new TemplateHandler(basedir, UTF8));

        assertEquals("dir-does-not-exist does not exist.", thr.getMessage());
    }

    private static class StringOutputStream extends OutputStream {
        private StringBuilder sb = new StringBuilder();

        @Override
        public void write(final int b) throws IOException {
            this.sb.append((char) b);
        }

        @Override
        public String toString() {
            return this.sb.toString();
        }

        public void reset() {
            sb.setLength(0);
        }
    }
}
