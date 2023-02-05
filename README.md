
# Properties Constants Creator Maven Plugin

Maven plugin that creates Java constants for properties files.

## Example

For this example the following properties file
`src/main/resources/messages.properties` is assumed.

```
welcome.user=Hello user
goodby.user=Have a nice day
```

The plugin is configure with the mandatory option `<basePackage>`:

```xml
<plugin>
    <groupId>de.r3s6.maven</groupId>
    <artifactId>properties-constants-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>example</id>
            <configuration>
                <basePackage>de.r3s6.maven.example</basePackage>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This results in the Java class `Messages`:

```java
package de.r3s6.maven.example;

/**
 * Constants for messages.properties
 * <p>
 * The constant values are the keys to access the properties.
 *
 * @author properties-constants-maven-plugin
 */
public final class Messages {

    /**
     * Properties file used to generate this class: "messages.properties".
     * <p>
     * If this name should be used to load the properties via classpath,
     * a leading slash ('/') might be needed.
     */
    public final static String PROPERTIES_FILE_NAME = "messages.properties";

    /**
     * Key of welcome.user=Hello user
     */
    public final static String WELCOME_USER = "welcome.user";

    /**
     * Key of goodby.user=Have a nice day
     */
    public final static String GOODBY_USER = "goodby.user";

    /** Hidden constructor. */
    private Messages() {
        // nothing to instantiate
    }

}
```

The exact content of the generated class depends on the chosen code template
and configuration.

BTW: It is also possible to use a custom [Freemarker] template.

# Build

Full build with:
```
mvn clean install site -Pit,coverage
```
This builds the plugin, runs integration tests with coverage analysis,
installs  the plugin to your local repo and finally builds the documentation
web site.

[Freemarker]: https://freemarker.apache.org/

