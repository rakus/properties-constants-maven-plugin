<#ftl encoding="utf-8">
<#--
  Copyright 2021 Ralf Schandl

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<#assign genLoadBundle            = ((options.genLoadBundle!"false") == "true")>
<#assign genLoadProperties        = ((options.genLoadProperties!"false") == "true")>
<#assign genGetPropertiesFilename = ((options.genGetPropertiesFilename!"false") == "true")>
<#assign genGetBundleName         = ((options.genGetBundleName!"false") == "true")>
package ${pkgName};
<#if genLoadBundle>

import java.util.Locale;
import java.util.ResourceBundle;
</#if>
<#if genLoadProperties>

import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
</#if>

/**
 * Constants for ${propertiesFileName}
 * <p>
 * The constant values are the keys to access the properties.
 *
 * @author properties-constants-maven-plugin
 */
public final class ${simpleClassName} {
<#list entries as entry>

    /**
     * Key of ${entry.key}=${entry.value}
     */
    public final static String ${entry.constantName} = "${entry.key}";
</#list>

    /** Hidden constructor. */
    private ${simpleClassName}() {
        // nothing to instantiate
    }
<#if genGetPropertiesFilename>

    /**
     * Returns the filename of the properties file used to generate
     * this class.
     *
     * @return always "${propertiesFileName}"
     */
    public static String getPropertiesFilename() {
        return "${propertiesFileName}";
    }
</#if>
<#if genLoadProperties>

    /**
     * Loads the properties file "${propertiesFileName}" from the classpath.
     * @return the loaded properties
     * @throws IOException if properties file not found or on load problems
     */
    public static Properties loadProperties() throws IOException {
        final Properties properties = new Properties();
        try (final InputStream stream = ${simpleClassName}.class.getResourceAsStream("/${propertiesFileName}")) {
            if(stream == null) {
                throw new IOException("Resource not found: ${propertiesFileName}");
            }
<#if isXmlProperties>
            properties.loadFromXML(stream);
<#else>
            properties.load(stream);
</#if>
        }
        return properties;
    }
</#if>
<#if genGetBundleName>

    /**
     * Returns the bundle name - this is the properties file name
     * used to generate this class excluding extension and locale part.
     *
     * @return always "${bundleName}"
     */
    public static String getBundleName() {
        return "${bundleName}";
    }
</#if>
<#if genLoadBundle>

    /**
     * Loads the resource bundle "${bundleName}" for the default locale.
     * @return the loaded bundle
     * @throws MissingResourceException if bundle couldn't be found
     */
    public static ResourceBundle loadBundle() {
        return ResourceBundle.getBundle("${bundleName}");
    }

    /**
     * Loads the resource bundle "${bundleName}" for the given locale.
     * @param locale the locale to use
     * @return the loaded bundle
     * @throws MissingResourceException if bundle couldn't be found
     * @throws NullPointerException if locale is null
     */
    public static ResourceBundle loadBundle(final Locale locale) {
        return ResourceBundle.getBundle("${bundleName}", locale);
    }
</#if>

}
