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
</#list>

    /** Hidden constructor. */
    private ${simpleClassName}() {
        // nothing to instantiate
    }
}
