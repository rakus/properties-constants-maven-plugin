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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.lang.model.SourceVersion;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.Scanner;
import org.sonatype.plexus.build.incremental.BuildContext;

import freemarker.core.ParseException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * Goal to create Java constant classes from properties files.
 *
 * @author Ralf Schandl
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateMojo extends AbstractMojo {

    /**
     * Pattern to match the transition from lowercase to uppercase characters.
     */
    private static final Pattern SMALL_BIG = Pattern.compile("([a-z])([A-Z])");


    private static final String KEYS_TEMPLATE_ID = "keys";
    private static final String VALUES_TEMPLATE_ID = "values";
    private static final String DEFAULT_TEMPLATE_ID = KEYS_TEMPLATE_ID;

    private static final String KEY_TEMPLATE_FMT = "plugin-default-templates/%s-template.ftl";

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Component
    private BuildContext buildContext;

    /**
     * Whether to skip the plugin execution.
     */
    @Parameter(property = "properties-constants.skip")
    private boolean skip;

    /**
     * Directory to search for properties files.
     */
    @Parameter(defaultValue = "src/main/resources")
    private File resourceDir;

    /**
     * File patterns of files to include.
     */
    @Parameter(defaultValue = "*.properties")
    private String[] includes;

    /**
     * File patterns of files to exclude.
     */
    @Parameter
    private String[] excludes;

    /**
     * Output directory for generated java files.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/prop-constants")
    private File outputDir;

    /**
     * Specifies the character encoding of the generated Java files.
     */
    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    private String sourceEncoding;

    /**
     * Specifies the Java package of the generated Java classes.
     */
    @Parameter(required = true)
    private String basePackage;

    /**
     * Whether all constants file end up in base package independent of the
     * directory structure.
     * <p>
     * By default the constants Java classes of properties files located in
     * sub-directories of {@code resourceDir} will be put in a matching sub-packages
     * of {@code basePackage}.
     * <p>
     * If {@code flattenPackage} is true, all classes will end up in
     * {@code basePackage}. Note that this might lead to name collisions.
     */
    @Parameter(defaultValue = "false")
    private boolean flattenPackage;

    /**
     * Suffix to append to generated class names.
     * <p>
     * Must be a valid java name by itself,
     * {@code SourceVersion.isIdentifier(classNameSuffix))} must return true.
     */
    @Parameter(defaultValue = "")
    private String classNameSuffix = "";

    /**
     * Template id or file name.
     * <p>
     * The plugin provides the templates <code>keys</code> and <code>values</code>.
     * <p>
     * A file name can be given for a custom Freemarker template.
     */
    @Parameter(defaultValue = DEFAULT_TEMPLATE_ID)
    private String template = DEFAULT_TEMPLATE_ID;

    /**
     * Additional options for the selected template.
     * <p>
     * The template <code>keys</code> supports the following options:
     * <dl>
     * <dt>genGetPropertiesFilename</dt>
     * <dd>generate the method <code>getPropertiesFilename()</code></dd>
     * <dt>genLoadProperties</dt>
     * <dd>generate the method <code>loadProperties()</code></dd>
     * <dt>genGetBundleName</dt>
     * <dd>generate the method <code>getBundleName()</code></dd>
     * <dt>genLoadBundle</dt>
     * <dd>generate the method <code>loadBundle()</code> and
     * <code>loadBundle(Locale)</code></dd>
     * </dl>
     */
    @Parameter
    private Map<String, String> templateOptions = new HashMap<>();

    private TemplateHandler tmplHandler;

    private List<String> errorMessages = new ArrayList<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        checkConfig();

        if (!skip) {

            checkResourceDir();

            tmplHandler = new TemplateHandler(project.getBasedir(), sourceEncoding);

            cleanupDeletes();

            final Collection<GeneratorRequest> genRequests = scanProperties();

            if (genRequests.isEmpty()) {
                getLog().info("No properties files found - no Java classes to generate");
            } else {
                getLog().info("Generating " + genRequests.size() + " Java constant class"
                        + (genRequests.size() == 1 ? "" : "es"));
            }

            for (final GeneratorRequest genReq : genRequests) {
                getLog().debug("Generating constants for " + genReq.getPropertiesFileName() + ": "
                        + genReq.getFullClassName());
                createConstants(genReq);
            }
        } else {
            getLog().info("Skipped - skip == true");
        }

        if (outputDir.isDirectory()) {
            project.addCompileSourceRoot(outputDir.getPath());
        }

        if (!errorMessages.isEmpty()) {
            throw new MojoExecutionException(errorMessages.stream().collect(Collectors.joining("\n")));
        }
    }

    private void checkConfig() throws MojoExecutionException {
        if (!SourceVersion.isName(basePackage)) {
            throw new MojoExecutionException("Configured basePackage \"" + basePackage + "\" is invalid.");
        }

        if (classNameSuffix.length() != 0 && !SourceVersion.isIdentifier(classNameSuffix)) {
            throw new MojoExecutionException("Configured classNameSuffix \"" + classNameSuffix + "\" is invalid.");
        }
    }

    private void checkResourceDir() throws MojoExecutionException {

        if (!resourceDir.exists()) {
            throw new MojoExecutionException("Configured resourceDir \"" + resourceDir + "\" does not exist.");
        }

        if (!resourceDir.isDirectory()) {
            throw new MojoExecutionException("Configured resourceDir \"" + resourceDir + "\" is not a directory.");
        }
    }

    /**
     * Scan for properties files.
     *
     * @return Collection of GeneratorRequests
     */
    private Collection<GeneratorRequest> scanProperties() {
        final Scanner scanner = buildContext.newScanner(resourceDir);
        scanner.setIncludes(includes);
        scanner.setExcludes(excludes);
        scanner.scan();

        final Map<String, GeneratorRequest> genRequests = new LinkedHashMap<>();
        for (final String propFile : scanner.getIncludedFiles()) {
            final GeneratorRequest gr = new GeneratorRequest(resourceDir, outputDir, basePackage, propFile,
                    flattenPackage, classNameSuffix);
            if (!genRequests.containsKey(gr.getFullClassName())) {
                genRequests.put(gr.getFullClassName(), gr);
            } else {
                addError(gr.getPropertiesFile(), 0, 0, "Would create same constant class " + gr.getFullClassName()
                        + " as " + genRequests.get(gr.getFullClassName()).getPropertiesFileName());
            }
        }
        return genRequests.values();
    }

    private void cleanupDeletes() {
        /*
         * The delete scanner will only find something when run within Eclipse.
         */
        final Scanner scanner = buildContext.newDeleteScanner(resourceDir);
        scanner.setIncludes(includes);
        scanner.setExcludes(excludes);
        scanner.scan();

        for (final String propFile : scanner.getIncludedFiles()) {
            final GeneratorRequest gr = new GeneratorRequest(resourceDir, outputDir, basePackage, propFile,
                    flattenPackage, classNameSuffix);
            if (gr.getJavaFile().exists()) {
                try {
                    Files.delete(gr.getJavaFile().toPath());
                    buildContext.refresh(gr.getJavaFile());
                } catch (NoSuchFileException e) {
                    // IGNORED: file was already gone -- fine
                } catch (IOException e) {
                    addError(gr.getJavaFile(), 0, 0,
                            "Could not delete: " + gr.getJavaFile() + " (" + e + ")", e);
                }
            }
        }
    }

    private void createConstants(final GeneratorRequest genReq) throws MojoExecutionException {

        final File propFile = genReq.getPropertiesFile();

        buildContext.removeMessages(propFile);

        final List<PropEntry> entries = new ArrayList<>();
        try (InputStream is = new FileInputStream(propFile)) {
            final Properties props = new OrderedProperties();
            if (genReq.isXmlProperties()) {
                props.loadFromXML(is);
            } else {
                props.load(is);
            }
            props.keySet().stream().forEach(k -> entries.add(new PropEntry((String) k, props.getProperty((String) k))));

        } catch (final IOException e) {
            addError(propFile, 0, 0, "Error loading properties file: " + e.getMessage(), e);
            return;
        } catch (final IllegalArgumentException e) {
            addError(propFile, 0, 0, "Properties file " + propFile + " contains invalid key: " + e.getMessage(), e);
        }

        final File javaFile = genReq.getJavaFile();

        javaFile.getParentFile().mkdirs();

        try {
            createStringConstants(genReq, entries, javaFile);
        } catch (TemplateNotFoundException e) {
            throw new MojoExecutionException("Code template not found: " + e.getTemplateName(), e);
        } catch (final ParseException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (final IOException e) {
            addError(genReq.getPropertiesFile(), 0, 0,
                    "Error generating Java file " + genReq.getJavaFileName() + " (" + e.toString() + ")", e);
        } catch (TemplateException e) {
            final String tmplName = e.getEnvironment().getMainTemplate().getName();
            throw new MojoExecutionException("Error in template processing: " + tmplName + " (" + e.getMessage() + ")",
                    e);
        }
    }

    private void createStringConstants(final GeneratorRequest genRequest, final List<PropEntry> entries,
            final File javaFile) throws IOException, TemplateException {

        final Map<String, Object> model = buildModel(genRequest, entries);

        final String templateFile;

        switch (this.template) {
        case KEYS_TEMPLATE_ID:
        case VALUES_TEMPLATE_ID:
            templateFile =  String.format(KEY_TEMPLATE_FMT, this.template);
            break;

        default:
            templateFile = this.template;
            break;
        }

        // CSOFF: MultipleString
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(buildContext.newFileOutputStream(javaFile), sourceEncoding))) {

            tmplHandler.process(templateFile, model, pw);

        }
        // CSON: MultipleString
    }

    private Map<String, Object> buildModel(final GeneratorRequest genReq, final List<PropEntry> entries) {
        final Map<String, Object> model = new HashMap<>();

        model.put("pkgName", genReq.getPkgName());
        model.put("simpleClassName", genReq.getSimpleClassName());
        model.put("fullClassName", genReq.getFullClassName());
        model.put("propertiesFileName", genReq.getPropertiesFileName());
        model.put("javaFileName", genReq.getJavaFileName());
        model.put("bundleName", genReq.getBundleName());
        model.put("isXmlProperties", genReq.isXmlProperties());

        model.put("entries", entries);

        model.put("options", templateOptions);

        return model;
    }

    private void addError(final File file, final int line, final int column, final String message) {
        addError(file, line, column, message, null);
    }

    private void addError(final File file, final int line, final int column, final String message,
            final Throwable thr) {

        buildContext.addMessage(file, line, column, message, BuildContext.SEVERITY_ERROR, thr);
        errorMessages.add(String.format("%s[%d:%d] %s", file.getPath(), line, column, message));
    }

    /**
     * Translates a property key to a Java constant name.
     *
     * @param k the property key
     * @return the constant name, upper snake case
     */
    static String keyToConstant(final String k) {
        final String c = SMALL_BIG.matcher(k.trim()).replaceAll("$1_$2").toUpperCase();

        final StringBuilder sb = new StringBuilder();

        char chr = c.charAt(0);
        if (Character.isJavaIdentifierStart(chr)) {
            sb.append(chr);
        } else {
            sb.append("_");
            if (Character.isJavaIdentifierPart(chr)) {
                sb.append(chr);
            }
        }

        for (int i = 1; i < c.length(); i++) {
            chr = c.charAt(i);
            if (Character.isJavaIdentifierPart(chr)) {
                sb.append(chr);
            } else {
                sb.append("_");
            }
        }

        return sb.toString();
    }
}
