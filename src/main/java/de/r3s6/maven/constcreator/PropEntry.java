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

import java.util.Objects;

import de.r3s6.maven.constcreator.NameHandler.JavaNames;

/**
 * PropEntry describes a property file entry with generated Java names.
 * <p>
 * An instance of this class holds the key and value from the properties file
 * and additional the generated constant, variable and getter name.
 *
 * @author Ralf Schandl
 */
public class PropEntry {

    private final String key;
    private final String value;

    private final String constantName;
    private final String variableName;
    private final String getterName;

    /**
     * Constructs a new PropEntry.
     *
     * @param key   the key from the properties file entry. Must not be empty.
     * @param value the value from a property file entry
     * @throws NullPointerException        when key is null
     * @throws InvalidPropertyKeyException when key can't be transformed to a Java
     *                                     variable/constant name. Currently only
     *                                     thrown for empty keys
     */
    public PropEntry(final String key, final String value) {
        Objects.requireNonNull(key, "PropEntry.key must not be null");
        this.key = key.trim();
        if (this.key.length() == 0) {
            throw new InvalidPropertyKeyException("empty key");
        }
        this.value = value;

        final JavaNames names = NameHandler.createJavaNames(key);
        constantName = names.getConstantName();
        variableName = names.getVariableName();
        getterName =   names.getGetterName();
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

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
