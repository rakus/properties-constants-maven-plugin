package ${pkgName};

/**
 * Constants for ${propertiesFileName}
 *
 * @author properties-constants-maven-plugin
 */
public final class ${simpleClassName} {
<#list entries as entry>

    /**
     * Key of ${entry.key}=${entry.value}
     */
    public final static String ${entry.constantName}_KEY = "${entry.key}";

    /**
     * Value of ${entry.key}=${entry.value}
     */
    public final static String ${entry.constantName}_VALUE = "${entry.value?j_string}";
</#list>

    /** Hidden constructor. */
    private ${simpleClassName}() {
        // nothing to instantiate
    }
}
