package de.r3s6.maven.constcreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PropEntry describes a property file entry with generated Java names.
 * <p>
 * An instance of this class holds the key and value from the properties file
 * and additional the generated constant, variable and getter name.
 *
 * @author Ralf Schandl
 */
public class PropEntry {

    private static final String DIV = "_";
    private static final char DIV_CHR = DIV.charAt(0);

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
     * @throws NullPointerException     when key is null
     * @throws IllegalArgumentException when key is invalid (e.g. empty)
     */
    public PropEntry(final String key, final String value) {
        Objects.requireNonNull(key);
        this.key = key.trim();
        if (this.key.length() == 0) {
            throw new IllegalArgumentException("empty key");
        }
        this.value = value;

        final List<String> parts = splitParts(key);

        constantName = buildConstantName(parts);
        variableName = buildVariableName(parts);
        getterName = buildGetterName(parts);
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

    private List<String> splitParts(final String propKey) {
        final List<String> parts = new ArrayList<>();

        final StringBuilder sb = new StringBuilder();

        for (char chr : propKey.toCharArray()) {

            if (chr != DIV_CHR && Character.isJavaIdentifierPart(chr)) {
                sb.append(chr);
            } else if (sb.length() > 0) {
                parts.add(sb.toString());
                sb.setLength(0);
            }
        }

        if (sb.length() > 0) {
            parts.add(sb.toString());
        }

        return parts;

    }

    private String buildConstantName(final List<String> parts) {
        final String name = String.join(DIV, parts).toUpperCase();
        if (Character.isJavaIdentifierStart(name.charAt(0))) {
            return name;
        } else {
            return DIV + name;
        }

    }

    private String buildVariableName(final List<String> parts) {

        final StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1).toLowerCase());
        }

        if (Character.isJavaIdentifierStart(sb.charAt(0))) {
            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        } else {
            sb.insert(0, DIV);
        }

        return sb.toString();
    }

    private String buildGetterName(final List<String> parts) {
        final StringBuilder sb = new StringBuilder("get");

        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1).toLowerCase());
        }

        return sb.toString();
    }

}
