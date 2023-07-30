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
import java.util.ResourceBundle;

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

    /** Package name. */
    private final String packageName;

    /** Simple class name (without package). */
    private final String simpleClassName;

    /** Class name with package. */
    private final String fullClassName;

    /** The java output file name, relative to the output directory. */
    private final String javaFileName;

    /** the java output file. */
    private final File javaFile;

    /** File name of the property (relative to search dir). */
    private final String propertiesFileName;

    /** The properties file. */
    private final File propertiesFile;

    /** Whether the properties file is a xml file. */
    private final boolean xmlProperties;

    /**
     * Bundle name is file name with extension and language marker (e.g "_en")
     * removed.
     */
    private final String bundleName;

    // CSOFF: ParameterNumberCheck
    private GeneratorRequest(final String fullClassName, final String javaFileName, final File javaFile,
            final String propertiesFileName, final File propertiesFile, final boolean xmlProperties,
            final String bundleName) {
        this.fullClassName = fullClassName;
        this.javaFileName = javaFileName;
        this.javaFile = javaFile;
        this.propertiesFileName = propertiesFileName;
        this.propertiesFile = propertiesFile;
        this.xmlProperties = xmlProperties;
        this.bundleName = bundleName;

        final int lastDot = fullClassName.lastIndexOf('.');
        this.packageName = fullClassName.substring(0, lastDot);
        this.simpleClassName = fullClassName.substring(lastDot + 1);
    }
    // CSON: ParameterNumberCheck

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

    public String getPackageName() {
        return packageName;
    }

    public String getSimpleClassName() {
        return simpleClassName;
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
        return propertiesFileName;
    }

    public File getPropertiesFile() {
        return propertiesFile;
    }

    /**
     * The resource bundle name.
     * <p>
     * This is the file name of the properties file with extension and locale marker
     * (e.g "_en") removed and slashes replaced by dots.
     * <p>
     * E.g. {@code dir/messages_en_US.properties} becomes {@code dir.messages}.
     *
     * @return the bundle name
     */
    public String getBundleName() {
        return bundleName;
    }

    public boolean isXmlProperties() {
        return xmlProperties;
    }

    /**
     * Builder for a {@link GeneratorRequest}.
     */
    public static class Builder {

        /** Class name with package. */
        private String fullClassName;

        /** The java output file name, relative to the output directory. */
        private String javaFileName;

        /** the java output file. */
        private File javaFile;

        /** File name of the property (relative to search dir). */
        private String propertiesFileName;

        /** The properties file. */
        private File propertiesFile;

        /** Whether the properties file is a xml file. */
        private boolean xmlProperties;

        /**
         * Bundle name is file name with extension and language marker (e.g "_en")
         * removed.
         */
        private String bundleName;

        /**
         * Builds a {@link GeneratorRequest} from the builder content.
         *
         * @return a fresh GeneratorRequest
         */
        public GeneratorRequest build() {
            return new GeneratorRequest(fullClassName, javaFileName, javaFile, propertiesFileName, propertiesFile,
                    xmlProperties, bundleName);
        }

        /**
         * Sets the name of the class to create.
         *
         * @param className the fully qualified class name
         * @return this Builder
         */
        public Builder className(final String className) {
            this.fullClassName = className;
            return this;
        }

        /**
         * Sets the java file name to create.
         *
         * @param theJavaFileName the java file name relative to output directory.
         * @return this Builder
         */
        public Builder javaFileName(final String theJavaFileName) {
            this.javaFileName = theJavaFileName;
            return this;
        }

        /**
         * Sets the java file (including output directory).
         *
         * @param theJavaFile the java file
         * @return this Builder
         */
        public Builder javaFile(final File theJavaFile) {
            this.javaFile = theJavaFile;
            return this;
        }

        /**
         * Sets the properties file name.
         *
         * @param thePropertiesFileName the property file name relative to resource directory
         * @return this Builder
         */
        public Builder propertiesFileName(final String thePropertiesFileName) {
            this.propertiesFileName = thePropertiesFileName;
            return this;
        }

        /**
         * Sets the properties file (including resource directory).
         *
         * @param thePropertiesFile the properties file.
         * @return this Builder
         */
        public Builder propertiesFile(final File thePropertiesFile) {
            this.propertiesFile = thePropertiesFile;
            return this;
        }

        /**
         * Sets whether the properties file is a xml file, according to the file
         * extension.
         *
         * @param isXmlProperties whether the properties is a xml file
         * @return this Builder
         */
        public Builder xmlProperties(final boolean isXmlProperties) {
            this.xmlProperties = isXmlProperties;
            return this;
        }

        /**
         * Sets the name of the resource bundle. Suitable to load the properties file
         * via {@link ResourceBundle}.
         *
         * @param theBundleName the resource bundle name
         * @return this Builder
         */
        public Builder bundleName(final String theBundleName) {
            this.bundleName = theBundleName;
            return this;
        }
    }

}
