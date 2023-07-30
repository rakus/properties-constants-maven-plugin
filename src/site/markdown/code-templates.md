
# Code Templates

The plugin uses the [Freemarker] template engine to generate the Java source
code. While two templates (`keys` and `values`) are provided by default, it is
also possible to use a custom template for code generation.

## Provided Templates

### Template `keys`

The template `keys` creates a Java class that contains String constants holding
the property keys. This is useful to access the properties values at runtime.

As this requires to load the properties as `Properties` or `ResourceBundle`,
the template provides a constant with the properties file name and if configured
the resource bundle name.

### Options

#### `genPropertiesFilenameConstant`
Boolean (`true`/`false`), Default: `true`

Adds the constant `PROPERTIES_FILE_NAME` to the generated class. This holds
the name needed to load the properties file. The name of the constant can be
changed with the option `propertiesFilenameConstant`.

Typically `genPropertiesFilenameConstant` is set to `false` when
`genBundleNameConstant` is `true`.

#### `propertiesFilenameConstant`
String, Default: `PROPERTIES_FILE_NAME`

Name of the constant holding the properties file name. The value must be a
valid Java variable name.

#### `genBundleNameConstant`
Boolean (`true`/`false`), Default: `false`

Adds the constant `BUNDLE_NAME` to the generated class. This holds the bundle
name; hence, the name of the properties file without extension and locale
markers. The name of the constant can be changed with the option
`bundleNameConstant`.

Typically `genPropertiesFilenameConstant` is set to `false` when
`genBundleNameConstant` is `true`.

**NOTE**: According to the JavaDoc of `java.util.ResourceBundle`, the bundle name
should be a valid class name. The plugin does not validate the generated bundle
name, so it might be an invalid class name. On the other hand, `ResourceBundle`
seems be able to load resource bundles from files that are not valid class names.

#### `bundleNameConstant`
String, Default: `BUNDLE_NAME`

Name of the constant holding the bundle name. The value must be a valid Java
variable name.

#### Example

The following example shows the default configuration
```
<templateOptions>
    <genPropertiesFilenameConstant>true</genPropertiesFilenameConstant>
    <propertiesFilenameConstant>PROPERTIES_FILE_NAME</propertiesFilenameConstant>
    <genBundleNameConstant>false</genBundleNameConstant>
    <bundleNameConstant>BUNDLE_NAME</bundleNameConstant>
</templateOptions>
```

### Template `values`

The template `values` creates a Java class that contains String constants holding
the property values. Then the properties files itself are _not needed_ at runtime.

This template does not support any additional options.


## Custom Templates

If a custom template should be used, it might be useful to first have a look
into the [Freemarker documentation], especially the section "Template Author's
Guide".

The plugin assumes that the templates character encoding is the same as was
configured for source file to generate (property `sourceEncoding`, which defaults
to `${project.build.sourceEncoding}`).

If the template uses a different encoding (or just to be explicit), it should
start with a ftl directive defining the character encoding. Like:

```
<#ftl encoding="utf-8">
```

### The Data Model

The model used for Freemarker templates contain the following fields:

* `pkgName`: the java package name of the generated class
* `simpleClassName`: The name of the generated class without package
* `fullClassName`: the fully qualified class name including package
* `javaFileName`: Name of the generated Java file including the directories
  representing the Java package.
* `propertiesFileName`: name of the properties file relative to the
  `<resourceDir>`
* `bundleName`: Like `propertiesFileName` but without extension and locale
  marker and dots instead of slashes as separator.
* `isXmlProperties`: Whether the properties file is a XML file.
* `properties`: Properties read from the properties file.
* `entries`: List of entries to create constants. See below.
* `options`: Map of template options. See below.

### Properties

The field `properties` contains the Properties as they were loaded from the properties file.
This data is not used by the default templates, but may be useful in custom templates.

Note: To access properties that have dot in the name use `${properties["key.with.dots"]}`.

#### Entries

The data model contains the field `entry` that is a list of `Entry` objects.
Every `Entry` object represents an entry from the properties file for which a
constant should be created. It contains the following field:

* `key`: The key from the properties file entry.
* `value`: The value from the properties file entry.
* `constantName`: Name suitable for a Java constant derived from `key`.
* `variableName`: Name suitable for a Java variable derived from `key`.
* `getterName`: Name suitable for a getter method derived from `key`.

Example: For the following line of in the properties file:

```
welcome.message=Hello there
```

The following Entry would be created:

* `key`: `welcome.message`
* `value`: `Hello there`
* `constantName`: `WELCOME_MESSAGE`
* `variableName`: `welcomeMessage`
* `getterName`: `getWelcomeMessage`

#### Options

The field `options` in the model is a `Map<String,String>`. It's content is
configured in the pom like:

```
<templateOptions>
    <addCopyright>true</addCopyright>
    <company>ACME</company>
</templateOptions>
```

This makes the following variables accessible in the template:

* `${options.addCopyright}`
* `${options.company}`

Important: This are both of type `String`.

__Using Options as Booleans__

Strings they can't be directly used as a Boolean. Also it has to be handled
that they might be unset (aka `null`).  The simplest way is to define a boolean
variable in the context of the template:

```
<#assign addCopyright = ((options.addCopyright!"false") == "true")>
...
<#if addCopyright>
...
</#if>
```

[Freemarker]: https://freemarker.apache.org/
[Freemarker documentation]: https://freemarker.apache.org/docs/index.html

