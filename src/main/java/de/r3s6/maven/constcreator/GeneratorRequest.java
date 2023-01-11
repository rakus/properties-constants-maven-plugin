package de.r3s6.maven.constcreator;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds information about the requested code generation.
 * <p>
 * <b>Note</b>: {@link #equals(Object)} and {@link #hashCode()} only work with
 * the full classname.
 *
 * @author Ralf Schandl
 *
 */
// CSOFF: MultipleString
public final class GeneratorRequest {

    /** Pattern to match invalid variable name chars. */
    private static final Pattern INVALID_NAME_CHARS = Pattern.compile("[^A-Za-z0-9_]");

    /** Package name. */
    private final String pkgName;

    /** Simple class name (without package). */
    private final String className;

    /** Class name with package. */
    private final String fullClassName;

    /** The java output file name, relative to the output directory. */
    private final String javaFileName;

    /** the java output file. */
    private final File javaFile;

    /** File name of the property (relative to search dir). */
    private final String propertyFileName;

    /** The properties file. */
    private final File propertiesFile;

    /** Whether the properties file is a xml file. */
    private final boolean xmlProperties;

    /**
     * Bundle name is file name with extension and language marker (e.g "_en") removed.
     */
    private final String bundleName;

    GeneratorRequest(final File resourceDir, final File outputDir, final String pkgName, final String propFileName,
            final boolean flatten) {
        Objects.requireNonNull(resourceDir);
        Objects.requireNonNull(outputDir);
        Objects.requireNonNull(pkgName);
        Objects.requireNonNull(propFileName);

        if (pkgName.trim().length() == 0) {
            throw new IllegalArgumentException("Empty package name not supported");
        }

        this.propertyFileName = propFileName;

        this.xmlProperties = propFileName.toLowerCase().endsWith(".xml");

        // remove extension and locale stuff
        this.bundleName = propFileName.replaceAll("\\.[^.]*$", "").replaceAll("_[a-z][a-z](_[A-Z][A-Z])?$", "");

        // concat and replace file separators
        final String fqName;
        if (flatten) {
            fqName = (pkgName + "." + bundleName.replaceFirst("^.*[\\/]", "")).replaceAll("[\\/]", ".");
        } else {
            fqName = (pkgName + "." + bundleName).replaceAll("[\\/]", ".");
        }

        final int lastDotIdx = fqName.lastIndexOf('.');
        String pName = fqName.substring(0, lastDotIdx);
        String cName = fqName.substring(lastDotIdx + 1);

        // clean package name
        pName = pName.replaceAll("-", "");

        // clean class name
        final StringBuffer sb = new StringBuffer();
        final Matcher m = Pattern.compile("[-_.](.)").matcher(cName);
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);

        cName = INVALID_NAME_CHARS.matcher(sb.toString()).replaceAll("");
        cName = cName.substring(0, 1).toUpperCase() + cName.substring(1);

        this.pkgName = pName;
        this.className = cName;
        this.fullClassName = pName + "." + cName;

        this.javaFileName = this.fullClassName.replace('.', '/') + ".java";

        this.propertiesFile = new File(resourceDir, this.propertyFileName);
        this.javaFile = new File(outputDir, getJavaFileName());

    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof GeneratorRequest) {
            final GeneratorRequest other = (GeneratorRequest) obj;
            return this.fullClassName.equals(other.fullClassName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 17 * fullClassName.hashCode();
    }

    public String getPkgName() {
        return pkgName;
    }

    public String getSimpleClassName() {
        return className;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public String getJavaFileName() {
        return javaFileName;
    }

    public File getJavaFile() {
        return javaFile;
    }

    public String getPropertiesFileName() {
        return propertyFileName;
    }

    public File getPropertiesFile() {
        return propertiesFile;
    }

    /**
     * The resource bundle name.
     * <p>
     * This is the file name of the properties file with extension and locale marker
     * (e.g "_en") removed.
     * <p>
     * E.g. {@code dir/messages_en_US.properties} becomes {@code dir/messages}.
     *
     * @return the bundle name
     */
    public String getBundleName() {
        return bundleName;
    }

    public boolean isXmlProperties() {
        return xmlProperties;
    }
}
