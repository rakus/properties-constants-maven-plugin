<#ftl encoding="utf-8">
<#--
  Copyright 2023 Ralf Schandl

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
<#assign genPropertiesFilenameConstant = ((options.genPropertiesFilenameConstant!"true") == "true")>
<#assign propertiesFilenameConstant    = options.propertiesFilenameConstant!"PROPERTIES_FILE_NAME">
<#assign genBundleNameConstant         = ((options.genBundleNameConstant!"false") == "true")>
<#assign bundleNameConstant            = options.bundleNameConstant!"BUNDLE_NAME">
package ${pkgName};

/**
 * Constants for ${propertiesFileName}
 * <p>
 * The constant values are the keys to access the properties.
 *
 * @author properties-constants-maven-plugin
 */
public final class ${simpleClassName} {
<#if genPropertiesFilenameConstant>

    /**
     * Properties file used to generate this class: "${propertiesFileName}".
     * <p>
     * If this name should be used to load the properties via classpath,
     * a leading slash ('/') might be needed.
     */
    public final static String ${propertiesFilenameConstant} = "${propertiesFileName}";
</#if>
<#if genBundleNameConstant>

    /**
     * ResourceBundle used to generate this class: "${bundleName}"
     */
    public final static String ${bundleNameConstant} = "${bundleName}";
</#if>
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

}
