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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sonatype.plexus.build.incremental.BuildContext;

@ExtendWith(MockitoExtension.class)
class GenerateMojoTest {

    @Mock
    private MavenProject mavenProject;

    @Mock
    private BuildContext buildContext;

    @Mock
    private Scanner deleteScanner;

    @Mock
    private Scanner propertiesScanner;

    private TestMavenLog mavenLog = new TestMavenLog();

    @AfterEach
    void cleanup() {
        mavenLog.reset();
    }

    @Test
    void testInvalidBasePackage() {

        GenerateMojo mojo = createMojo("com.assert.hallo");

        MojoExecutionException thr = assertThrows(MojoExecutionException.class, () -> mojo.execute());

        assertEquals("Configured basePackage \"com.assert.hallo\" is invalid.", thr.getMessage());
    }

    @Test
    void testInvalidClassNameSuffix() {

        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "classNameSuffix", "%Hello");

        MojoExecutionException thr = assertThrows(MojoExecutionException.class, () -> mojo.execute());

        assertEquals("Configured classNameSuffix \"%Hello\" is invalid.", thr.getMessage());
    }

    @Test
    void testNotExistingResourceDir() {

        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "resourceDir", new File("does-not-exist"));

        MojoExecutionException thr = assertThrows(MojoExecutionException.class, () -> mojo.execute());

        assertEquals("Configured resourceDir \"does-not-exist\" does not exist.", thr.getMessage());
    }

    @Test
    void testResourceDirIsFile() {

        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "resourceDir", new File("checkstyle.xml"));

        MojoExecutionException thr = assertThrows(MojoExecutionException.class, () -> mojo.execute());

        assertEquals("Configured resourceDir \"checkstyle.xml\" is not a directory.", thr.getMessage());
    }


    @Test
    void testNoPropertyFiles(@TempDir final File projectBaseDir) throws MojoExecutionException, MojoFailureException {

        when(mavenProject.getBasedir()).thenReturn(projectBaseDir);
        when(buildContext.newDeleteScanner(any(File.class))).thenReturn(deleteScanner);
        when(deleteScanner.getIncludedFiles()).thenReturn(new String[0]);

        when(buildContext.newScanner(any(File.class))).thenReturn(propertiesScanner);
        when(propertiesScanner.getIncludedFiles()).thenReturn(new String[0]);

        // create target directory

        File tgtDir = createDir(projectBaseDir, "target");

        File resourceDir = createDir(projectBaseDir, "src/main/resources");

        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "outputDir", tgtDir);
        inject(mojo, "resourceDir", resourceDir);

        mojo.execute();

        mavenLog.assertContainsSubString("[info] No properties files found - no Java classes to generate");
    }

    @Test
    void testOnePropertyFiles(@TempDir final File projectBaseDir)
            throws MojoExecutionException, MojoFailureException, IOException {

        when(mavenProject.getBasedir()).thenReturn(projectBaseDir);
        when(buildContext.newDeleteScanner(any(File.class))).thenReturn(deleteScanner);
        when(deleteScanner.getIncludedFiles()).thenReturn(new String[0]);

        when(buildContext.newScanner(any(File.class))).thenReturn(propertiesScanner);
        when(propertiesScanner.getIncludedFiles()).thenReturn(new String[] { "test.properties" });

        when(buildContext.newFileOutputStream(any(File.class)))
                .thenAnswer(i -> new FileOutputStream((File) i.getArguments()[0]));

        // create target directory
        File tgtDir = createDir(projectBaseDir, "target");

        File resourceDir = createDir(projectBaseDir, "src/main/resources");

        File propertyFile = createFile(resourceDir, "test.properties");

        try (FileWriter fos = new FileWriter(propertyFile)) {
            fos.append("welcome.message=Hello there");
        } catch (IOException e) {
            fail(e.getMessage());
        }

        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "outputDir", tgtDir);
        inject(mojo, "resourceDir", resourceDir);

        mojo.execute();

        mavenLog.assertContainsSubString("[info] Generating 1 Java constant class");
        mavenLog.assertContainsSubString("[debug] Generating constants for test.properties: de.r3s6.maven.Test");
    }

    @Test
    void testXmlPropertyFiles(@TempDir final File projectBaseDir)
            throws MojoExecutionException, MojoFailureException, IOException {

        when(mavenProject.getBasedir()).thenReturn(projectBaseDir);
        when(buildContext.newDeleteScanner(any(File.class))).thenReturn(deleteScanner);
        when(deleteScanner.getIncludedFiles()).thenReturn(new String[0]);

        when(buildContext.newScanner(any(File.class))).thenReturn(propertiesScanner);
        when(propertiesScanner.getIncludedFiles()).thenReturn(new String[] { "test.xml" });

        when(buildContext.newFileOutputStream(any(File.class)))
                .thenAnswer(i -> new FileOutputStream((File) i.getArguments()[0]));

        // create target directory
        File tgtDir = createDir(projectBaseDir, "target");

        File resourceDir = createDir(projectBaseDir, "src/main/resources");

        File propertyFile = createFile(resourceDir, "test.xml");

        try (PrintWriter fos = new PrintWriter(new FileWriter(propertyFile))) {
            fos.println("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
            fos.println("<properties>");
            fos.println("<entry key=\"test0001\">Hallo</entry>");
            fos.println("</properties>");
        } catch (IOException e) {
            fail(e.getMessage());
        }

        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "outputDir", tgtDir);
        inject(mojo, "resourceDir", resourceDir);

        mojo.execute();

        mavenLog.assertContainsSubString("[info] Generating 1 Java constant class");
        mavenLog.assertContainsSubString("[debug] Generating constants for test.xml: de.r3s6.maven.Test");
    }

    @Test
    void testInvalidXmlPropertyFiles(@TempDir final File projectBaseDir)
            throws MojoExecutionException, MojoFailureException, IOException {

        when(mavenProject.getBasedir()).thenReturn(projectBaseDir);
        when(buildContext.newDeleteScanner(any(File.class))).thenReturn(deleteScanner);
        when(deleteScanner.getIncludedFiles()).thenReturn(new String[0]);

        when(buildContext.newScanner(any(File.class))).thenReturn(propertiesScanner);
        when(propertiesScanner.getIncludedFiles()).thenReturn(new String[] { "test.xml" });

        // create target directory
        File tgtDir = createDir(projectBaseDir, "target");

        File resourceDir = createDir(projectBaseDir, "src/main/resources");

        File propertyFile = createFile(resourceDir, "test.xml");

        try (PrintWriter fos = new PrintWriter(new FileWriter(propertyFile))) {
            fos.println("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
            fos.println("<properties>");
            fos.println("<entry key=\"test0001\">Hallo</entry>");
        } catch (IOException e) {
            fail(e.getMessage());
        }

        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "outputDir", tgtDir);
        inject(mojo, "resourceDir", resourceDir);

        MojoExecutionException thr = assertThrows(MojoExecutionException.class, () -> mojo.execute());

        assertTrue(thr.getMessage().contains("test.xml[0:0] Error loading properties file:"), thr.getMessage());
    }

    @Test
    void testCustomTemplateNotFound(@TempDir final File projectBaseDir) throws IOException {

        when(mavenProject.getBasedir()).thenReturn(projectBaseDir);
        when(buildContext.newDeleteScanner(any(File.class))).thenReturn(deleteScanner);
        when(deleteScanner.getIncludedFiles()).thenReturn(new String[0]);

        when(buildContext.newScanner(any(File.class))).thenReturn(propertiesScanner);
        when(propertiesScanner.getIncludedFiles()).thenReturn(new String[] { "test.properties" });

        when(buildContext.newFileOutputStream(any(File.class)))
                .thenAnswer(i -> new FileOutputStream((File) i.getArguments()[0]));

        // create target directory
        File tgtDir = createDir(projectBaseDir, "target");

        File resourceDir = createDir(projectBaseDir, "src/main/resources");

        File propertyFile = createFile(resourceDir, "test.properties");

        try (FileWriter fos = new FileWriter(propertyFile)) {
            fos.append("welcome.message=Hello there");
        } catch (IOException e) {
            fail(e.getMessage());
        }

        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "outputDir", tgtDir);
        inject(mojo, "resourceDir", resourceDir);
        inject(mojo, "template", "does-not-exist.ftl");

        MojoExecutionException thr = assertThrows(MojoExecutionException.class, () -> mojo.execute());

        assertEquals("Code template not found: does-not-exist.ftl", thr.getMessage());
    }

    @Test
    void testInvalidCustomTemplate(@TempDir final File projectBaseDir) throws IOException {

        when(mavenProject.getBasedir()).thenReturn(projectBaseDir);
        when(buildContext.newDeleteScanner(any(File.class))).thenReturn(deleteScanner);
        when(deleteScanner.getIncludedFiles()).thenReturn(new String[0]);

        when(buildContext.newScanner(any(File.class))).thenReturn(propertiesScanner);
        when(propertiesScanner.getIncludedFiles()).thenReturn(new String[] { "test.properties" });

        when(buildContext.newFileOutputStream(any(File.class)))
                .thenAnswer(i -> new FileOutputStream((File) i.getArguments()[0]));

        // create target directory
        File tgtDir = createDir(projectBaseDir, "target");

        File resourceDir = createDir(projectBaseDir, "src/main/resources");

        File propertyFile = createFile(resourceDir, "test.properties");

        try (FileWriter fos = new FileWriter(propertyFile)) {
            fos.append("welcome.message=Hello there");
        } catch (IOException e) {
            fail(e.getMessage());
        }

        File templateFile = createFile(projectBaseDir, "invalid-template.ftl");
        try (FileWriter fos = new FileWriter(templateFile)) {
            fos.append("<#list entries as entry>");
        } catch (IOException e) {
            fail(e.getMessage());
        }


        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "outputDir", tgtDir);
        inject(mojo, "resourceDir", resourceDir);
        inject(mojo, "template", "invalid-template.ftl");

        MojoExecutionException thr = assertThrows(MojoExecutionException.class, () -> mojo.execute());

        assertTrue(thr.getMessage().contains("Syntax error in template \"invalid-template.ftl\""), thr.getMessage());
    }

    @Test
    void testCannotCreateJavaFile(@TempDir final File projectBaseDir)
            throws MojoExecutionException, MojoFailureException, IOException {

        when(mavenProject.getBasedir()).thenReturn(projectBaseDir);
        when(buildContext.newDeleteScanner(any(File.class))).thenReturn(deleteScanner);
        when(deleteScanner.getIncludedFiles()).thenReturn(new String[0]);

        when(buildContext.newScanner(any(File.class))).thenReturn(propertiesScanner);
        when(propertiesScanner.getIncludedFiles()).thenReturn(new String[] { "test.properties" });

        when(buildContext.newFileOutputStream(any(File.class)))
                .thenAnswer(i -> new FileOutputStream((File) i.getArguments()[0]));

        // create target directory
        File tgtDir = createDir(projectBaseDir, "target");

        createDir(tgtDir, "de/r3s6/maven/Test.java");

        File resourceDir = createDir(projectBaseDir, "src/main/resources");

        File propertyFile = createFile(resourceDir, "test.properties");

        try (FileWriter fos = new FileWriter(propertyFile)) {
            fos.append("welcome.message=Hello there");
        } catch (IOException e) {
            fail(e.getMessage());
        }

        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "outputDir", tgtDir);
        inject(mojo, "resourceDir", resourceDir);

        MojoExecutionException thr = assertThrows(MojoExecutionException.class, () -> mojo.execute());

        assertTrue(thr.getMessage().matches(".*test.properties\\[0:0\\] Error generating Java file de.r3s6.maven.Test\\.java.*"), thr.getMessage());
    }

    @Test
    void testDeletedPropertyFiles(@TempDir final File projectBaseDir)
            throws MojoExecutionException, MojoFailureException, IOException {

        when(mavenProject.getBasedir()).thenReturn(projectBaseDir);
        when(buildContext.newDeleteScanner(any(File.class))).thenReturn(deleteScanner);
        when(deleteScanner.getIncludedFiles()).thenReturn(new String[] { "test.properties", "test2.properties" });

        when(buildContext.newScanner(any(File.class))).thenReturn(propertiesScanner);
        when(propertiesScanner.getIncludedFiles()).thenReturn(new String[0]);

        // create target directory
        File tgtDir = createDir(projectBaseDir, "target");

        File javaFile = createFile(projectBaseDir, "target/de/r3s6/maven/Test.java");
        assertTrue(javaFile.isFile());

        File resourceDir = createDir(projectBaseDir, "src/main/resources");

        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "outputDir", tgtDir);
        inject(mojo, "resourceDir", resourceDir);

        mojo.execute();

        assertFalse(javaFile.exists());
    }

    @Test
    void testCannotDeleteJavaFile(@TempDir final File projectBaseDir)
            throws MojoExecutionException, MojoFailureException, IOException {

        when(mavenProject.getBasedir()).thenReturn(projectBaseDir);
        when(buildContext.newDeleteScanner(any(File.class))).thenReturn(deleteScanner);
        when(deleteScanner.getIncludedFiles()).thenReturn(new String[] { "test.properties" });

        when(buildContext.newScanner(any(File.class))).thenReturn(propertiesScanner);
        when(propertiesScanner.getIncludedFiles()).thenReturn(new String[0]);

        // create target directory
        File tgtDir = createDir(projectBaseDir, "target");

        File javaDir = createDir(projectBaseDir, "target/de/r3s6/maven/Test.java");
        assertTrue(javaDir.isDirectory());
        createFile(javaDir, "some-file");

        File resourceDir = createDir(projectBaseDir, "src/main/resources");

        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "outputDir", tgtDir);
        inject(mojo, "resourceDir", resourceDir);

        MojoExecutionException thr = assertThrows(MojoExecutionException.class, () -> mojo.execute());

        assertTrue(thr.getMessage().matches(".*Test.java\\[0:0\\] Could not delete:.*"), thr.getMessage());
        assertTrue(javaDir.exists());
    }

    @Test
    void testDuplicateClassname(@TempDir final File projectBaseDir)
            throws MojoExecutionException, MojoFailureException, IOException {

        when(mavenProject.getBasedir()).thenReturn(projectBaseDir);
        when(buildContext.newDeleteScanner(any(File.class))).thenReturn(deleteScanner);
        when(deleteScanner.getIncludedFiles()).thenReturn(new String[0]);

        when(buildContext.newScanner(any(File.class))).thenReturn(propertiesScanner);
        when(propertiesScanner.getIncludedFiles()).thenReturn(new String[] { "test-case.properties", "test_case.properties" });

        when(buildContext.newFileOutputStream(any(File.class)))
                .thenAnswer(i -> new FileOutputStream((File) i.getArguments()[0]));

        // create target directory
        File tgtDir = createDir(projectBaseDir, "target");

        File resourceDir = createDir(projectBaseDir, "src/main/resources");
        File propertyFile = createFile(resourceDir, "test-case.properties");

        try (PrintWriter fos = new PrintWriter(new FileWriter(propertyFile))) {
            fos.println("welcome.message=Hello there");
        } catch (IOException e) {
            fail(e.getMessage());
        }

        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "outputDir", tgtDir);
        inject(mojo, "resourceDir", resourceDir);

        MojoExecutionException thr = assertThrows(MojoExecutionException.class, () -> mojo.execute());

        assertTrue(thr.getMessage().contains("test_case.properties[0:0] Would create same constant class de.r3s6.maven.TestCase as test-case.properties"), thr.getMessage());
    }

    @Test
    void testMojoSkip() throws MojoExecutionException, MojoFailureException {
        GenerateMojo mojo = createMojo("de.r3s6.maven");
        inject(mojo, "skip", true);

        inject(mojo, "outputDir", new File("target"));

        mojo.execute();

        mavenLog.assertContainsSubString("[info] Skipped - skip == true");
    }

    @Test
    void testBuildGeneratorRequest() throws MojoExecutionException, MojoFailureException {
        GenerateMojo mojo = createMojo("pkg");

        inject(mojo, "outputDir", new File("output-dir"));
        inject(mojo, "resourceDir", new File("input-dir"));

        GeneratorRequest gr = mojo.buildGeneratorRequest("test.properties");
        assertEquals("test.properties", gr.getPropertiesFileName());
        assertEquals("test", gr.getBundleName());
        assertEquals("pkg.Test", gr.getFullClassName());
        assertEquals("pkg", gr.getPackageName());
        assertEquals("Test", gr.getSimpleClassName());
        assertEquals("pkg/Test.java", gr.getJavaFileName());
        assertEquals(new File("input-dir/test.properties"), gr.getPropertiesFile());
        assertEquals(new File("output-dir/pkg/Test.java"), gr.getJavaFile());
        assertFalse(gr.isXmlProperties());

        gr = mojo.buildGeneratorRequest("test-case.properties");
        assertEquals("test-case.properties", gr.getPropertiesFileName());
        // test-case is not a valid bundle name, we set it anyways
        assertEquals("test-case", gr.getBundleName());
        assertEquals("pkg.TestCase", gr.getFullClassName());
        assertEquals("pkg", gr.getPackageName());
        assertEquals("TestCase", gr.getSimpleClassName());
        assertEquals("pkg/TestCase.java", gr.getJavaFileName());
        assertEquals(new File("input-dir/test-case.properties"), gr.getPropertiesFile());
        assertEquals(new File("output-dir/pkg/TestCase.java"), gr.getJavaFile());
        assertFalse(gr.isXmlProperties());
    }

    @Test
    void testBuildGeneratorRequestNoExt() throws MojoExecutionException, MojoFailureException {
        GenerateMojo mojo = createMojo("pkg");

        inject(mojo, "outputDir", new File("output-dir"));
        inject(mojo, "resourceDir", new File("input-dir"));

        final GeneratorRequest gr = mojo.buildGeneratorRequest("test-case");
        assertEquals("test-case", gr.getPropertiesFileName());
        // test-case is not a valid bundle name, we set it anyways
        assertEquals("test-case", gr.getBundleName());
        assertEquals("pkg.TestCase", gr.getFullClassName());
        assertEquals("pkg", gr.getPackageName());
        assertEquals("TestCase", gr.getSimpleClassName());
        assertEquals("pkg/TestCase.java", gr.getJavaFileName());
        assertEquals(new File("input-dir/test-case"), gr.getPropertiesFile());
        assertEquals(new File("output-dir/pkg/TestCase.java"), gr.getJavaFile());
        assertFalse(gr.isXmlProperties());
    }

    @Test
    void testBuildGeneratorRequestSubDir() throws MojoExecutionException, MojoFailureException {
        GenerateMojo mojo = createMojo("pkg");

        inject(mojo, "outputDir", new File("output-dir"));
        inject(mojo, "resourceDir", new File("input-dir"));

        GeneratorRequest gr = mojo.buildGeneratorRequest("test/test.properties");
        assertEquals("test/test.properties", gr.getPropertiesFileName());
        assertEquals("test.test", gr.getBundleName());
        assertEquals("pkg.test.Test", gr.getFullClassName());
        assertEquals("pkg.test", gr.getPackageName());
        assertEquals("Test", gr.getSimpleClassName());
        assertEquals("pkg/test/Test.java", gr.getJavaFileName());
        assertEquals(new File("input-dir/test/test.properties"), gr.getPropertiesFile());
        assertEquals(new File("output-dir/pkg/test/Test.java"), gr.getJavaFile());
        assertFalse(gr.isXmlProperties());

        gr = mojo.buildGeneratorRequest("test-case/test.properties");
        assertEquals("test-case/test.properties", gr.getPropertiesFileName());
        assertEquals("test-case.test", gr.getBundleName());
        assertEquals("pkg.testcase.Test", gr.getFullClassName());
        assertEquals("pkg.testcase", gr.getPackageName());
        assertEquals("Test", gr.getSimpleClassName());
        assertEquals("pkg/testcase/Test.java", gr.getJavaFileName());
        assertEquals(new File("input-dir/test-case/test.properties"), gr.getPropertiesFile());
        assertEquals(new File("output-dir/pkg/testcase/Test.java"), gr.getJavaFile());
        assertFalse(gr.isXmlProperties());
    }

    @Test
    void testBuildGeneratorRequestSubDirWeirdChars() throws MojoExecutionException, MojoFailureException {
        GenerateMojo mojo = createMojo("pkg");

        inject(mojo, "outputDir", new File("output-dir"));
        inject(mojo, "resourceDir", new File("input-dir"));

        GeneratorRequest gr = mojo.buildGeneratorRequest("0test/test.properties");
        assertEquals("0test/test.properties", gr.getPropertiesFileName());
        assertEquals("0test.test", gr.getBundleName());
        assertEquals("pkg._0test.Test", gr.getFullClassName());
        assertEquals("pkg._0test", gr.getPackageName());
        assertEquals("Test", gr.getSimpleClassName());
        assertEquals("pkg/_0test/Test.java", gr.getJavaFileName());
        assertEquals(new File("input-dir/0test/test.properties"), gr.getPropertiesFile());
        assertEquals(new File("output-dir/pkg/_0test/Test.java"), gr.getJavaFile());
        assertFalse(gr.isXmlProperties());

        gr = mojo.buildGeneratorRequest("%test/test.properties");
        assertEquals("%test/test.properties", gr.getPropertiesFileName());
        assertEquals("%test.test", gr.getBundleName());
        assertEquals("pkg._test.Test", gr.getFullClassName());
        assertEquals("pkg._test", gr.getPackageName());
        assertEquals("Test", gr.getSimpleClassName());
        assertEquals("pkg/_test/Test.java", gr.getJavaFileName());
        assertEquals(new File("input-dir/%test/test.properties"), gr.getPropertiesFile());
        assertEquals(new File("output-dir/pkg/_test/Test.java"), gr.getJavaFile());
        assertFalse(gr.isXmlProperties());
    }


    @Test
    void testBuildGeneratorRequestFlatten() throws MojoExecutionException, MojoFailureException {
        GenerateMojo mojo = createMojo("pkg");

        inject(mojo, "outputDir", new File("output-dir"));
        inject(mojo, "resourceDir", new File("input-dir"));
        inject(mojo, "flattenPackage", true);

        final GeneratorRequest gr = mojo.buildGeneratorRequest("test/test.properties");
        assertEquals("test/test.properties", gr.getPropertiesFileName());
        assertEquals("test.test", gr.getBundleName());
        assertEquals("pkg.Test", gr.getFullClassName());
        assertEquals("pkg", gr.getPackageName());
        assertEquals("Test", gr.getSimpleClassName());
        assertEquals("pkg/Test.java", gr.getJavaFileName());
        assertEquals(new File("input-dir/test/test.properties"), gr.getPropertiesFile());
        assertEquals(new File("output-dir/pkg/Test.java"), gr.getJavaFile());
        assertFalse(gr.isXmlProperties());
    }

    @Test
    void testBuildGeneratorRequestSuffix() throws MojoExecutionException, MojoFailureException {
        GenerateMojo mojo = createMojo("pkg");

        inject(mojo, "outputDir", new File("output-dir"));
        inject(mojo, "resourceDir", new File("input-dir"));
        inject(mojo, "flattenPackage", true);
        inject(mojo, "classNameSuffix", "Suffix");

        final GeneratorRequest gr = mojo.buildGeneratorRequest("test/test.properties");
        assertEquals("test/test.properties", gr.getPropertiesFileName());
        assertEquals("test.test", gr.getBundleName());
        assertEquals("pkg.TestSuffix", gr.getFullClassName());
        assertEquals("pkg", gr.getPackageName());
        assertEquals("TestSuffix", gr.getSimpleClassName());
        assertEquals("pkg/TestSuffix.java", gr.getJavaFileName());
        assertEquals(new File("input-dir/test/test.properties"), gr.getPropertiesFile());
        assertEquals(new File("output-dir/pkg/TestSuffix.java"), gr.getJavaFile());
        assertFalse(gr.isXmlProperties());
    }

    @Test
    void testBuildGeneratorRequestLocale() throws MojoExecutionException, MojoFailureException {
        GenerateMojo mojo = createMojo("pkg");

        inject(mojo, "outputDir", new File("output-dir"));
        inject(mojo, "resourceDir", new File("input-dir"));

        GeneratorRequest gr = mojo.buildGeneratorRequest("test/test_en.properties");
        assertEquals("test/test_en.properties", gr.getPropertiesFileName());
        assertEquals("test.test", gr.getBundleName());
        assertEquals("pkg.test.Test", gr.getFullClassName());
        assertEquals("pkg.test", gr.getPackageName());
        assertEquals("Test", gr.getSimpleClassName());
        assertEquals("pkg/test/Test.java", gr.getJavaFileName());
        assertEquals(new File("input-dir/test/test_en.properties"), gr.getPropertiesFile());
        assertEquals(new File("output-dir/pkg/test/Test.java"), gr.getJavaFile());
        assertFalse(gr.isXmlProperties());

        gr = mojo.buildGeneratorRequest("test/test_en_US.properties");
        assertEquals("test/test_en_US.properties", gr.getPropertiesFileName());
        assertEquals("test.test", gr.getBundleName());
        assertEquals("pkg.test.Test", gr.getFullClassName());
        assertEquals("pkg.test", gr.getPackageName());
        assertEquals("Test", gr.getSimpleClassName());
        assertEquals("pkg/test/Test.java", gr.getJavaFileName());
        assertEquals(new File("input-dir/test/test_en_US.properties"), gr.getPropertiesFile());
        assertEquals(new File("output-dir/pkg/test/Test.java"), gr.getJavaFile());
        assertFalse(gr.isXmlProperties());
    }

    @Test
    void testBuildGeneratorRequestXml() throws MojoExecutionException, MojoFailureException {
        GenerateMojo mojo = createMojo("pkg");

        inject(mojo, "outputDir", new File("output-dir"));
        inject(mojo, "resourceDir", new File("input-dir"));

        GeneratorRequest gr = mojo.buildGeneratorRequest("test/test.xml");
        assertEquals("test/test.xml", gr.getPropertiesFileName());
        assertEquals("test.test", gr.getBundleName());
        assertEquals("pkg.test.Test", gr.getFullClassName());
        assertEquals("pkg.test", gr.getPackageName());
        assertEquals("Test", gr.getSimpleClassName());
        assertEquals("pkg/test/Test.java", gr.getJavaFileName());
        assertEquals(new File("input-dir/test/test.xml"), gr.getPropertiesFile());
        assertEquals(new File("output-dir/pkg/test/Test.java"), gr.getJavaFile());
        assertTrue(gr.isXmlProperties());

        gr = mojo.buildGeneratorRequest("test/test.xml-props");
        assertEquals("test/test.xml-props", gr.getPropertiesFileName());
        assertEquals("test.test", gr.getBundleName());
        assertEquals("pkg.test.Test", gr.getFullClassName());
        assertEquals("pkg.test", gr.getPackageName());
        assertEquals("Test", gr.getSimpleClassName());
        assertEquals("pkg/test/Test.java", gr.getJavaFileName());
        assertEquals(new File("input-dir/test/test.xml-props"), gr.getPropertiesFile());
        assertEquals(new File("output-dir/pkg/test/Test.java"), gr.getJavaFile());
        assertTrue(gr.isXmlProperties());
    }

    @Test
    void testRemoveResourceBundleLocale()
    {
        GenerateMojo mojo = createMojo("pkg");

        assertEquals("test", mojo.removeResourceBundleLocale("test_de"));
        assertEquals("test", mojo.removeResourceBundleLocale("test_de_DE"));
        assertEquals("test", mojo.removeResourceBundleLocale("test_de_DE_Unix"));
        assertEquals("test", mojo.removeResourceBundleLocale("test_de_Latn_DE"));
        assertEquals("test", mojo.removeResourceBundleLocale("test_de_Latn_DE_Unix"));
    }

    /**
     * Create GenerateMojo with basePackage and some base config.
     *
     * @param basePackage the basePackage to configure
     * @return the partly configured Mojo
     */
    private GenerateMojo createMojo(final String basePackage) {
        GenerateMojo mojo = new GenerateMojo();
        inject(mojo, "project", mavenProject);
        inject(mojo, "buildContext", buildContext);
        inject(mojo, "basePackage", basePackage);
        inject(mojo, "classNameSuffix", "");
        inject(mojo, "sourceEncoding", "UTF-8");
        mavenLog.reset();
        mojo.setLog(mavenLog);
        return mojo;
    }

    /**
     * Inject a value into a field of an object.
     *
     * @param target    the target object
     * @param fieldName name of the field to set
     * @param value     the value to assign
     */
    private void inject(final Object target, final String fieldName, final Object value) {
        List<Field> fields = ReflectionSupport.findFields(target.getClass(),
                f -> fieldName.equals(f.getName()) && f.getDeclaringClass().equals(target.getClass()),
                HierarchyTraversalMode.BOTTOM_UP);

        assertEquals(1, fields.size());

        Field field = fields.get(0);
        boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new AssertionError("Error setting " + field, e);
        } finally {
            field.setAccessible(accessible);
        }
    }

    /**
     * Create a directory below baseDir.
     *
     * @param baseDir the base directory
     * @param dir     the directory name. Can be multiple levels.
     * @return the File representing the created directory.
     */
    private File createDir(final File baseDir, final String dir) {
        File tgtDir = new File(baseDir, dir);
        tgtDir.mkdirs();
        return tgtDir;
    }

    /**
     * Create a file below baseDir.
     *
     * @param baseDir the base directory
     * @param dir     the file name. Can have additional directory levels.
     * @return the File representing the created file.
     */
    private File createFile(final File baseDir, final String fileName) {
        File file = new File(baseDir, fileName);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            fail("Creating " + file + " failed: " + e.toString());
        }

        return file;
    }

}
