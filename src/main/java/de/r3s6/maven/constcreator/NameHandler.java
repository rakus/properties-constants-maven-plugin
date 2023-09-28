package de.r3s6.maven.constcreator;
/*
 * Copyright 2023 Ralf Schandl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Utilities class with name-related methods.
 *
 * @author Ralf Schandl
 */
public final class NameHandler {

    private static final String DIV = "_";
    private static final char DIV_CHR = '_';

    private NameHandler() {
        // Nothing to instantiate.
    }

    /**
     * Creates a java constant, variable and getter name from the given name.
     * <p>
     * The given name is split into parts at lower-upper change, underscore and
     * non-identifier chars. The parts are then combined into new names suitable for
     * a java constant, variable or getter-method.
     *
     * @param name the initial name (aka property key)
     *
     * @return {@link JavaNames} containing names
     * @throws IllegalArgumentException if name is empty String
     * @throws NullPointerException     if name is null
     */
    public static JavaNames createJavaNames(final String name) {
        final List<String> np = splitParts(name);
        return new JavaNames(buildConstantName(np), buildVariableName(np), buildGetterName(np));
    }

    /**
     * Build a class name for the given name.
     *
     * @param name the name to build a class name
     * @return a java class name
     * @throws IllegalArgumentException if name is empty String
     * @throws NullPointerException     if name is null
     */
    public static String createTypeName(final String name) {
        final String typename = camelCase(splitParts(name));
        if (Character.isJavaIdentifierStart(typename.charAt(0))) {
            return typename;
        } else {
            return DIV + typename;
        }
    }

    /**
     * Splits the name into parts at lower-upper change, underscore and
     * non-identifier chars.
     *
     * @param name the name to split.
     * @return a list of name parts, all lower case
     */
    private static List<String> splitParts(final String name) {
        Objects.requireNonNull(name, "name must not be null");
        if (name.length() == 0) {
            throw new IllegalArgumentException("name must not be empty");
        }

        final List<String> parts = new ArrayList<>();

        final StringBuilder sb = new StringBuilder();

        boolean lastIsLower = false;

        for (char chr : name.toCharArray()) {

            final boolean isUpper = Character.isUpperCase(chr);

            if (chr != DIV_CHR && Character.isJavaIdentifierPart(chr)) {
                if (lastIsLower && isUpper) {
                    parts.add(sb.toString().toLowerCase(Locale.US));
                    sb.setLength(0);
                }
                sb.append(chr);
                lastIsLower = !isUpper;
            } else if (sb.length() > 0) {
                parts.add(sb.toString().toLowerCase(Locale.US));
                sb.setLength(0);
                lastIsLower = false;
            }
        }

        if (sb.length() > 0) {
            parts.add(sb.toString().toLowerCase(Locale.US));
        }

        if (parts.isEmpty()) {
            final char[] fake = new char[name.length()];
            Arrays.fill(fake, '_');
            parts.add(new String(fake)); // NOCS: IllegalInstantiation
        }

        return parts;
    }

    /**
     * Concatenate parts with underscore and make it upper-case.
     *
     * @param parts parts to concatenate
     * @return name suitable as Java constant name
     */
    private static String buildConstantName(final List<String> parts) {
        final String name = String.join(DIV, parts).toUpperCase(Locale.US);
        if (Character.isJavaIdentifierStart(name.charAt(0))) {
            return name;
        } else {
            return DIV + name;
        }
    }

    /**
     * Concatenate parts lower-camel-case.
     *
     * @param parts parts to concatenate
     * @return name suitable as Java variable name
     */
    private static String buildVariableName(final List<String> parts) {

        String name = camelCase(parts);
        name = Character.toLowerCase(name.charAt(0)) + name.substring(1);

        if (Character.isJavaIdentifierStart(name.charAt(0))) {
            return name;
        } else {
            return DIV + name;
        }
    }

    /**
     * Concatenate parts camel-case with leading "get".
     *
     * @param parts parts to concatenate
     * @return name suitable as Java getter method name
     */
    private static String buildGetterName(final List<String> parts) {
        return "get" + camelCase(parts);
    }

    private static String camelCase(final List<String> parts) {
        final StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1).toLowerCase());
        }

        return sb.toString();
    }

    /**
     * Provides different name types created from a property key.
     */
    public static final class JavaNames {
        private final String constantName;
        private final String variableName;
        private final String getterName;

        private JavaNames(final String constantName, final String variableName, final String getterName) {
            this.constantName = constantName;
            this.variableName = variableName;
            this.getterName = getterName;
        }

        public String getConstantName() {
            return constantName;
        }

        public String getVariableName() {
            return variableName;
        }

        public String getGetterName() {
            return getterName;
        }
    }
}
