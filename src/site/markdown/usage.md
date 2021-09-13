# Usage

## Project Directories

The following diagram shows the default layout used by the properties-constants-maven-plugin

    Project Root
     |
     +- src/main/resources             # directory to scan for properties files
     |
     +- target
          |
          +- generated-sources/prop-constants
                                       # directory to store generated java files

## Simple properties

Reads `*.properties` files from `src/main/resources` and generates Java classes
in the package `de.r3s6.constants`. Support methods for Properties are generated.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>de.r3s6.maven</groupId>
            <artifactId>properties-constants-maven-plugin</artifactId>
            <version>0.1.0</version>
            <configuration>
                <resourceDir>src/main/resources</resourceDir>
                <includes>
                    <include>*.properties</include>
                </includes>
                <basePackage>de.r3s6.constants</basePackage>
                <genGetPropertiesFilename>true</genGetPropertiesFilename>
                <genPropertiesLoader>true</genPropertiesLoader>
            </configuration>
            <executions>
                <execution>
                    <phase>generate-sources</phase>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```


## I18N - ResourceBundle

Reads ResourceBundle properties files from `src/main/resources/i18n`. It ignores
files with a locale marker (like `messages_en.properties`). Support methods for
ResourceBundles are generated.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>de.r3s6.maven</groupId>
            <artifactId>properties-constants-maven-plugin</artifactId>
            <version>0.1.0</version>
            <configuration>
                <resourceDir>src/main/resources/i18n</resourceDir>
                <includes>
                    <include>*.properties</include>
                </includes>
                <excludes>
                    <exclude>*_??.properties</exclude>
                </excludes>
                <basePackage>de.r3s6.constants.i18n</basePackage>
                <genGetBundleName>true</genGetBundleName>
                <genBundleLoader>true</genBundleLoader>
            </configuration>
            <executions>
                <execution>
                    <phase>generate-sources</phase>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

