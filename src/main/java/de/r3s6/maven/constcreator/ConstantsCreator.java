package de.r3s6.maven.constcreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

/**
 * Goal to create Java constant classes from properties files.
 *
 * @author Ralf Schandl
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class ConstantsCreator extends AbstractMojo {

    /**
     * Pattern to match the transition from lowercase to uppercase characters.
     */
    private static final Pattern SMALL_BIG = Pattern.compile("([a-z])([A-Z])");

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Component
    private BuildContext buildContext;

    /**
     * Whether to skip the plugin execution.
     */
    @Parameter(property = "const-creator.skip")
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
     * sub-directories of {@code resourceDir} will be put in sub-packages of
     * {@code basePackage}.
     * <p>
     * If {@code flattenPackage} is true, all classes will end up in
     * {@code basePackage}. Note that this might lead to name collisions.
     */
    @Parameter(defaultValue = "false")
    private boolean flattenPackage;

    /**
     * Generate methods to return the properties file name.
     * <p>
     * The name is relative to {@code resourceDir} parameter.
     * <p>
     * <code>public static String getPropertiesFilename()</code><br>
     */
    @Parameter(defaultValue = "false")
    private boolean genGetPropertiesFilename;

    /**
     * Generate a method to load the properties file via class path.
     * <p>
     * <code>public static Properties loadProperties() throws IOException</code>
     */
    @Parameter(defaultValue = "false")
    private boolean genPropertiesLoader;

    /**
     * Generate methods to return the bundle name.
     * <p>
     * This is the properties file name without extension and locale part.
     * <p>
     * The name is relative to {@code resourceDir} parameter.
     * <p>
     * <code>public static String getBundleName()</code><br>
     */
    @Parameter(defaultValue = "false")
    private boolean genGetBundleName;

    /**
     * Generate methods to load the properties file as ResourceBundle.
     * <p>
     * <code>public static ResourceBundle loadBundle()</code><br>
     * <code>public static ResourceBundle loadBundle(Locale locale)</code>
     */
    @Parameter(defaultValue = "false")
    private boolean genBundleLoader;

    private List<String> errorMessages = new ArrayList<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!skip) {
            if (!isValidPackageName(basePackage)) {
                throw new MojoExecutionException("Configures basePackage \"" + basePackage + "\" is invalid.");
            }

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
        }

        project.addCompileSourceRoot(outputDir.getPath());

        if (!errorMessages.isEmpty()) {
            throw new MojoExecutionException(errorMessages.stream().collect(Collectors.joining("\n")));
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
                    flattenPackage);
            if (!genRequests.containsKey(gr.getFullClassName())) {
                genRequests.put(gr.getFullClassName(), gr);
            } else {
                addError(gr.getPropertiesFile(), 1, 1, "Would creates same constant class " + gr.getFullClassName()
                        + " as " + genRequests.get(gr.getFullClassName()).getPropertiesFileName());
            }
        }
        return genRequests.values();
    }

    private void cleanupDeletes() {
        final Scanner scanner = buildContext.newDeleteScanner(resourceDir);
        scanner.setIncludes(includes);
        scanner.setExcludes(excludes);
        scanner.scan();

        for (final String propFile : scanner.getIncludedFiles()) {
            final GeneratorRequest gr = new GeneratorRequest(resourceDir, outputDir, basePackage, propFile,
                    flattenPackage);
            if (gr.getJavaFile().exists()) {
                gr.getJavaFile().delete();
                buildContext.refresh(gr.getJavaFile());
            }
        }
    }

    private void createConstants(final GeneratorRequest genReq) {

        final File propFile = genReq.getPropertiesFile();

        buildContext.removeMessages(propFile);

        final Map<String, String> entries = new LinkedHashMap<>();
        try (InputStream is = new FileInputStream(propFile)) {
            final Properties props = new OrderedProperties();
            if (genReq.isXmlProperties()) {
                props.loadFromXML(is);
            } else {
                props.load(is);
            }
            props.keySet().stream().forEach(k -> entries.put((String) k, props.getProperty((String) k)));

        } catch (final IOException e) {

            addError(propFile, 1, 1, "Error loading properties file: " + e.getMessage(), e);
            return;
        }

        if (entries.containsKey("")) {
            addError(propFile, 1, 1, "File contains entry with empty key", null);
            return;
        }

        final File javaFile = genReq.getJavaFile();

        javaFile.getParentFile().mkdirs();

        try {
            createStringConstants(genReq, entries, javaFile);
        } catch (final IOException e) {
            addError(genReq.getPropertiesFile(), 1, 1, "Error generating Java file " + genReq.getJavaFileName(), e);
        }
    }

    private void createStringConstants(final GeneratorRequest genRequest, final Map<String, String> entries,
            final File javaFile) throws IOException, FileNotFoundException {

        // CSOFF: MultipleString
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(buildContext.newFileOutputStream(javaFile), sourceEncoding))) {
            pw.printf("package %s;%n", genRequest.getPkgName());
            pw.println();
            if (genBundleLoader) {
                pw.println("import java.util.Locale;");
                pw.println("import java.util.ResourceBundle;");
            }
            if (genPropertiesLoader) {
                pw.println("import java.util.Properties;");
                pw.println("import java.io.IOException;");
                pw.println("import java.io.InputStream;");
            }
            pw.println();

            pw.printf("/**%n");
            pw.printf(" * Constants for %s.%n", genRequest.getPropertiesFileName());
            pw.printf(" * %n");
            pw.printf(" * @author properties-constants-maven-plugin%n");
            pw.printf(" */%n");
            pw.printf("public final class %s {%n", genRequest.getSimpleClassName());
            pw.println();

            for (final Entry<String, String> entry : entries.entrySet()) {
                final String k = entry.getKey();
                final String v = entry.getValue();
                pw.printf("    /**%n");
                pw.printf("     * %s=%s%n", k, v);
                pw.printf("     */%n");
                pw.printf("    public final static String %s = \"%s\";\n", keyToConstant(k), k);
                pw.printf("\n");
            }

            pw.printf("    /** Hidden constructor. */%n");
            pw.printf("    private %s() {%n", genRequest.getSimpleClassName());
            pw.printf("        // nothing to instantiate%n");
            pw.printf("    }%n");
            pw.println();

            if (genGetBundleName) {
                pw.printf("    /**%n");
                pw.printf("     * Returns the bundle name - this is the properties file name%n");
                pw.printf("     * used to generate this class excluding extension and locale part.%n");
                pw.printf("     * %n");
                pw.printf("     * @returns always \"%s\"%n", genRequest.getPropertiesBasename());
                pw.printf("     */%n");
                pw.printf("    public static String getBundleName() {%n");
                pw.printf("        return \"%s\";%n", genRequest.getPropertiesBasename());
                pw.printf("    }%n");
                pw.println();
            }

            if (genBundleLoader) {
                pw.printf("    /**%n");
                pw.printf("     * Loads the resource bundle \"%s\" for the default locale.%n",
                        genRequest.getPropertiesBasename());
                pw.printf("     * @returns the loaded bundle%n");
                pw.printf("     */%n");
                pw.printf("    public static ResourceBundle loadBundle() {%n");
                pw.printf("        return ResourceBundle.getBundle(\"%s\");%n", genRequest.getPropertiesBasename());
                pw.printf("    }%n");
                pw.println();

                pw.printf("    /**%n");
                pw.printf("     * Loads the resource bundle \"%s\" for the given locale.%n",
                        genRequest.getPropertiesBasename());
                pw.printf("     * @param locale the locale to use%n");
                pw.printf("     * @returns the loaded bundle%n");
                pw.printf("     */%n");
                pw.printf("    public static ResourceBundle loadBundle(final Locale locale) {%n");
                pw.printf("        return ResourceBundle.getBundle(\"%s\", locale);%n",
                        genRequest.getPropertiesBasename());
                pw.printf("    }%n");
                pw.println();
            }

            if (genGetPropertiesFilename) {
                pw.printf("    /**%n");
                pw.printf("     * Returns the filename of the properties file used to generate%n");
                pw.printf("     * this class.%n");
                pw.printf("     * %n");
                pw.printf("     * @returns always \"%s\"%n", genRequest.getPropertiesFileName());
                pw.printf("     */%n");
                pw.printf("    public static String getPropertiesFilename() {%n");
                pw.printf("        return \"%s\";%n", genRequest.getPropertiesFileName());
                pw.printf("    }%n");
                pw.println();
            }

            if (genPropertiesLoader) {
                pw.printf("    /**%n");
                pw.printf("     * Loads the properties file \"/%s\" from the classpath.%n",
                        genRequest.getPropertiesFileName());
                pw.printf("     * @returns the loaded properties%n");
                pw.printf("     * @throws IOException on load problems%n");
                pw.printf("     */%n");
                pw.printf("    public static Properties loadProperties() throws IOException {%n");
                pw.printf("        final Properties properties = new Properties();%n");
                pw.printf("        try (final InputStream stream = %s.class.getResourceAsStream(\"/%s\")) {%n",
                        genRequest.getSimpleClassName(), genRequest.getPropertiesFileName());
                if (genRequest.isXmlProperties()) {
                    pw.printf("            properties.loadFromXML(stream);%n");
                } else {
                    pw.printf("            properties.load(stream);%n");
                }
                pw.printf("        }%n");
                pw.printf("        return properties;%n");
                pw.printf("    }%n");
                pw.println();
            }

            pw.printf("}%n");
        }
        // CSON: MultipleString
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

    /**
     * Checks whether the given name is a valid package name.
     *
     * @param pkgName the name to check
     * @return whether the given name is a valid package name
     */
    static boolean isValidPackageName(final String pkgName) {
        if (pkgName == null || pkgName.trim().isEmpty()) {
            return false;
        }

        final String[] parts = pkgName.split("\\.");

        for (final String p : parts) {
            if (!SourceVersion.isName(p)) {
                return false;
            }
        }

        return true;
    }

}
