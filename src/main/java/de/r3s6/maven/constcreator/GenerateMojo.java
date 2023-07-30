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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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

    private static final String KEYS_TEMPLATE_ID = "keys";
    private static final String VALUES_TEMPLATE_ID = "values";
    private static final String DEFAULT_TEMPLATE_ID = KEYS_TEMPLATE_ID;

    private static final String KEY_TEMPLATE_FMT = "plugin-default-templates/%s-template.ftl";

    /**
     * Match locale marker of resource bundle properties files.
     * <ul>
     * <li>_de
     * <li>_de_DE
     * <li>_de_DE_Windows
     * <li>_de_Latn_DE
     * <li>_de_Latn_DE_Windows
     * </ul>
     */
    private static final Pattern RESOURCE_BUNDLE_LOCALE_PATTERN = Pattern
            .compile("_[a-z]{2}((_[A-z]{4})?_[A-Z]{2}(_\\w+)?)?$");

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
     * A file name can be given for a custom Freemarker template. File name lookup
     * is:
     * <ol>
     * <li>classpath</li>
     * <li>relative to project basedir</li>
     * </ol>
     */
    @Parameter(defaultValue = DEFAULT_TEMPLATE_ID)
    private String template = DEFAULT_TEMPLATE_ID;

    /**
     * Additional options for the selected template.
     * <p>
     * The template <code>keys</code> supports the following options:
     * <dl>
     * <dt>genPropertiesFilenameConstant</dt>
     * <dd>generate the constant <code>PROPERTIES_FILE_NAME</code></dd>
     * <dt>propertiesFilenameConstant</dt>
     * <dd>changes the name of the properties file name constant</dd>
     * <dt>genBundleNameConstant</dt>
     * <dd>generate the constant <code>BUNDLE_NAME</code></dd>
     * <dt>bundleNameConstant</dt>
     * <dd>changes the name of the bundle name constant</dd>
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
            final GeneratorRequest gr = buildGeneratorRequest(propFile);
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
            final GeneratorRequest gr = buildGeneratorRequest(propFile);
            if (gr.getJavaFile().exists()) {
                try {
                    Files.delete(gr.getJavaFile().toPath());
                    buildContext.refresh(gr.getJavaFile());
                } catch (NoSuchFileException e) {
                    // IGNORED: file already gone -- fine
                } catch (IOException e) {
                    addError(gr.getJavaFile(), 0, 0, "Could not delete: " + gr.getJavaFile() + " (" + e + ")", e);
                }
            }
        }
    }

    private void createConstants(final GeneratorRequest genReq) throws MojoExecutionException {

        final File propFile = genReq.getPropertiesFile();

        buildContext.removeMessages(propFile);

        final Properties props = new OrderedProperties();
        try (InputStream is = new FileInputStream(propFile)) {
            if (genReq.isXmlProperties()) {
                props.loadFromXML(is);
            } else {
                props.load(is);
            }
        } catch (final IOException e) {
            addError(propFile, 0, 0, "Error loading properties file: " + e.getMessage(), e);
            return;
        }

        final File javaFile = genReq.getJavaFile();

        javaFile.getParentFile().mkdirs();

        try {
            createStringConstants(genReq, props);
        } catch (TemplateNotFoundException e) {
            throw new MojoExecutionException("Code template not found: " + e.getTemplateName(), e);
        } catch (final ParseException e) {
            throw new MojoExecutionException("Error parsing template:" + e.getMessage(), e);
        } catch (TemplateException e) {
            final String tmplName = e.getEnvironment().getMainTemplate().getName();
            throw new MojoExecutionException("Error in template processing: " + tmplName + " (" + e.getMessage() + ")",
                    e);
        } catch (final IOException e) {
            addError(genReq.getPropertiesFile(), 0, 0,
                    "Error generating Java file " + genReq.getJavaFileName() + " (" + e.toString() + ")", e);
        } catch (final InvalidPropertyKeyException e) {
            addError(propFile, 0, 0, "Properties file " + propFile + " contains invalid key: " + e.getMessage(), e);
        }
    }

    /**
     * Creates the java constants file.
     *
     * @param genRequest {@link GeneratorRequest}
     * @param props      loaded properties
     * @throws ParseException              invalid template, thrown by Freemarker
     * @throws TemplateException           runtime problem in template processing,
     *                                     thrown by Freemarker
     * @throws TemplateNotFoundException   thrown by Freemarker
     * @throws IOException                 template loading or writing the Java file
     *                                     failed
     * @throws InvalidPropertyKeyException the properties contain a key that can't
     *                                     be translated to a Java variable/constant
     *                                     name
     */
    private void createStringConstants(final GeneratorRequest genRequest, final Properties props)
            throws IOException, TemplateException {

        final Map<String, Object> model = buildModel(genRequest, props);

        final String templateFile;

        switch (this.template) {
        case KEYS_TEMPLATE_ID:
        case VALUES_TEMPLATE_ID:
            templateFile = String.format(KEY_TEMPLATE_FMT, this.template);
            break;

        default:
            templateFile = this.template;
            break;
        }

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(buildContext.newFileOutputStream(genRequest.getJavaFile()), sourceEncoding))) {

            tmplHandler.process(templateFile, model, pw);

        }
    }

    private Map<String, Object> buildModel(final GeneratorRequest genReq, final Properties props) {

        final List<PropEntry> entryList = props.keySet().stream()
                .map(k -> new PropEntry((String) k, props.getProperty((String) k))).collect(Collectors.toList());

        final Map<String, Object> model = new HashMap<>();

        model.put("pkgName", genReq.getPackageName());
        model.put("simpleClassName", genReq.getSimpleClassName());
        model.put("fullClassName", genReq.getFullClassName());
        model.put("propertiesFileName", genReq.getPropertiesFileName());
        model.put("javaFileName", genReq.getJavaFileName());
        model.put("bundleName", genReq.getBundleName());
        model.put("isXmlProperties", genReq.isXmlProperties());

        model.put("entries", entryList);
        model.put("properties", props);

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

    // package visibility for testing
    GeneratorRequest buildGeneratorRequest(final String propertyFileName) {

        final GeneratorRequest.Builder builder = new GeneratorRequest.Builder();

        final String portableName = propertyFileName.replace('\\', '/');
        builder.propertiesFileName(portableName);
        builder.propertiesFile(new File(resourceDir, portableName));

        // Is it XML?
        final int lastDot = portableName.lastIndexOf('.');
        if (lastDot >= 0) {
            final String extension = portableName.substring(lastDot + 1);
            builder.xmlProperties(extension.toLowerCase(Locale.US).contains("xml"));
        }

        final Path path = Paths.get(portableName);
        final String basename = path.getFileName().toString();

        final int cnt = path.getNameCount();
        final List<String> nameParts = new ArrayList<>();
        if (!flattenPackage) {
            for (int x = 0; x < cnt - 1; x++) {
                final String name = path.getName(x).toString();
                if (SourceVersion.isName(name)) {
                    nameParts.add(name);
                } else {
                    nameParts.add(createValidJavaName(name));
                }
            }
        }

        nameParts.add(baseNametoClassname(basename) + classNameSuffix);

        // build java fully qualified class name
        final String className = basePackage + "." + String.join(".", nameParts);
        final String javaFilename = className.replace('.', '/') + ".java";

        builder.className(className);
        builder.javaFileName(javaFilename);
        builder.javaFile(new File(outputDir, javaFilename));

        nameParts.clear();
        for (int x = 0; x < cnt - 1; x++) {
            nameParts.add(path.getName(x).toString());
        }
        nameParts.add(baseNametoBundleName(basename));

        final String bundleName = String.join(".", nameParts);
        builder.bundleName(bundleName);

        if (getLog().isDebugEnabled() && !SourceVersion.isName(bundleName)) {
            getLog().debug("Bundle name is not a valid class name (might work anyways): " + bundleName);
        }

        return builder.build();
    }

    private String baseNametoClassname(final String basename) {
        return NameHandler.createTypeName(baseNametoBundleName(basename));
    }

    private String baseNametoBundleName(final String basename) {
        // remove extension
        final int idx = basename.lastIndexOf('.');
        final String name;
        if (idx > 0) {
            name = basename.substring(0, idx);
        } else {
            name = basename;
        }
        return removeResourceBundleLocale(name);
    }

    // package visibility for testing
    String removeResourceBundleLocale(final String name) {
        return RESOURCE_BUNDLE_LOCALE_PATTERN.matcher(name).replaceAll("");
    }

    private String createValidJavaName(final String name) {
        final StringBuilder sb = new StringBuilder();
        final char[] chrs = name.toCharArray();
        if (Character.isJavaIdentifierStart(chrs[0])) {
            sb.append(chrs[0]);
        } else if (Character.isJavaIdentifierPart(chrs[0])) {
            sb.append('_');
            sb.append(chrs[0]);
        } else {
            sb.append('_');
        }

        for (int x = 1; x < chrs.length; x++) {
            final char c = chrs[x];
            if (Character.isJavaIdentifierPart(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
