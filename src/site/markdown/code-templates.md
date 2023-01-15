
# Code Templates

The plugin uses the [Freemarker] template engine to generate the Java source
code. While two templates (`keys` and `values`) are provided by default, it is
also possible to use a custom template for code generation.

## Provided Templates

### Template `keys`

The template `keys` creates a Java class that contains String constants holding
the property keys. This is useful to access the properties values at runtime.

As this requires to load the properties as `Properties` or `ResourceBundle`,
the template can be configured to create methods to load these. Examples for
the created methods are shown in the [Introduction].

The following example enables ale possible options.

```
<templateOptions>
    <genGetPropertiesFilename>true</genGetPropertiesFilename>
    <genLoadProperties>true</genLoadProperties>
    <genGetBundleName>true</genGetBundleName>
    <genLoadBundle>true</genLoadBundle>
</templateOptions>
```

### Template `values`

The template `values` creates a Java class that contains String constants holding
the property values. Then the properties files itself are _not needed_ as runtime.

This template does not support any additional options.


## Custom Templates

If a custom template should be used, it might be useful to first have a look
into the [Freemarker documentation], especially the section "Template Author's
Guide".

The important part for the template author is to know the data model.

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
  marker.
* `isXmlProperties`: Whether the properties file is a XML file.
* `entries`: List of entries to create constants. See below.
* `options`: Map of template options. See below.

#### Entries

The data model contains the field `entry` that is a list of `Entry` objects.
Every `Entry` object represents an entry from the properties file for which a
constant should be created.  It contains the following field:

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
    <genLoadProperties>true</genLoadProperties>
    <company>ACME</company>
</templateOptions>
```

This makes the following variables accessible in the template:

* `${options.genLoadProperties}`
* `${options.company}`

Important: This are both of type `String`.

__Using Options as Booleans__

Strings they can't be directly used as a Boolean. Also it has to be handled
that they might be unset (aka `null`).  The simplest way is to define a boolean
variable in the context of the template:

```
<#assign genLoadProperties        = ((options.genLoadProperties!"false") == "true")>
...
<#if genLoadProperties>
...
</#if>
```

[Introduction]: /index.html#
[Freemarker]: https://freemarker.apache.org/
[Freemarker documentation]: https://freemarker.apache.org/docs/index.html

