
# Change Log for Properties Constants Creator Maven Plugin

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## unreleased

**This release contains breaking changes.**

## Fixed

* bundle name now uses dots as separator instead of slashes

### Changed

* **BREAKING**: Custom Freemarker template must be either on the classpath  or
  relative to project root. Absolute file names are no longer supported.
* **BREAKING** for custom Freemarker templates: The package name variable in
  the template model was renamed from `pkgName` to `packageName`.

## [0.4.0] - 2023-02-10

**This release contains breaking changes.**

### Fixed

### Added

### Changed

* **BREAKING**: Removed the possibility to add static methods to the classes
  generated with the template `keys`. As replacement a String constant holding
  the properties file name is created (`PROPERTIES_FILE_NAME`). For I18N a
  constant holding the bundle name can be created (`BUNDLE_NAME`). This behavior
  and the name of the constants is configurable.

## [0.3.0] - 2023-01-29

### Fixed

* Windows: backslash in file pathes result in invalid code generated
* Invalid syntax in examples in README and FAQ

### Added

* CI build with Windows

### Changed

* Property to skip is now `properties-constants.skip`


## [0.2.0] - 2023-01-20

The plugin is now using the Freemarker template engine to generate source
code. It defines two internal templates:

* `keys` generates constants that holds the keys to access the properties file
* `values` generate constants that holds the values from the properties file

See documentation for details.

### Changed

* use template engine Freemarker
* template options were renamed and are now below new tag `templateOptions`. See
  documentation

### Added

* option `classNameSuffix` to be appended to generated class name
* support for custom code templates


## 0.1.0 - 2021-10-17

Initial implementation.



[0.4.0]: https://github.com/rakus/properties-constants-maven-plugin/compare/v0.3.0...v0.4.0
[0.3.0]: https://github.com/rakus/properties-constants-maven-plugin/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/rakus/properties-constants-maven-plugin/compare/v0.1.0...v0.2.0

[//]:  vim:ft=markdown:ai:et:ts=4:spelllang=en_us:spell:tw=80
