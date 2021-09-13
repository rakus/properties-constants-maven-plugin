package de.r3s6.maven.constcreator;

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
final class GeneratorRequest {

    /** Pattern to match invalid variable name chars. */
    private static final Pattern INVALID_NAME_CHARS = Pattern.compile("[^A-Za-z0-9_]");

    /** Class name with package. */
    private final String fullClassName;

    /** Package name. */
    private final String pkgName;

    /** Simple class name (without package). */
    private final String className;

    /** File name of the property (relative to search dir). */
    private final String propertyFileName;

    private final boolean xmlProperties;

    /**
     * Basename is file name with extension and language marker (e.g "_en") removed.
     */
    private final String propertyBasename;

    GeneratorRequest(final String pkgName, final String propFileName, final boolean flatten) {

        this.propertyFileName = propFileName;

        this.xmlProperties = propFileName.toLowerCase().endsWith(".xml");

        // remove extension and locale stuff
        this.propertyBasename = propFileName.replaceAll("\\.[^.]*$", "").replaceAll("_[a-z][a-z](_[A-Z][A-Z])?$", "");

        // concat and replace file separators
        final String fqName;
        if (flatten) {
            fqName = (pkgName + "." + propertyBasename.replaceFirst("^.*[\\/]", "")).replaceAll("[\\/]", ".");
        } else {
            fqName = (pkgName + "." + propertyBasename).replaceAll("[\\/]", ".");
        }

        String pName;
        String cName;

        final int lastDotIdx = fqName.lastIndexOf('.');
        if (lastDotIdx >= 0) {
            pName = fqName.substring(0, lastDotIdx);
            cName = fqName.substring(lastDotIdx + 1);
        } else {
            // This would be the default package -- should never happen
            pName = "";
            cName = fqName;
        }

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

    /** The package name. */
    String getPkgName() {
        return pkgName;
    }

    /** The simple class name. */
    String getSimpleClassName() {
        return className;
    }

    /** Fully qualified class name with package. */
    String getFullClassName() {
        return fullClassName;
    }

    /** Name of the properties file relative to search dir. */
    public String getPropertiesFileName() {
        return propertyFileName;
    }

    /**
     * Basename is file name with extension and locale marker (e.g "_en") removed.
     * <p>
     * E.g. {@code dir/messages_en_US.properties} becomes {@code dir/messages}.
     */
    public String getPropertiesBasename() {
        return propertyBasename;
    }

    /**
     * File name of Java file relative to source folder.
     */
    String getJavaFileName() {
        return getFullClassName().replace('.', '/') + ".java";
    }

    public boolean isXmlProperties() {
        return xmlProperties;
    }
}
