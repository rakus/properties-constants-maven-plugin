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
package ${pkgName};

/**
 * Constants for ${propertiesFileName}
 * <p>
 * The constant values are the values of the properties.
 *
 * @author properties-constants-maven-plugin
 */
public final class ${simpleClassName} {
<#list entries as entry>

    /**
     * Value of ${entry.key}=${entry.value}
     */
    public final static String ${entry.constantName} = "${entry.value?j_string}";

/// MISSING END OF LIST

    /** Hidden constructor. */
    private ${simpleClassName}() {
        // nothing to instantiate
    }
}

