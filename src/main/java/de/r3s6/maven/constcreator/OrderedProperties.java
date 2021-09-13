package de.r3s6.maven.constcreator;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;

/**
 * Thin wrapper around {@link Properties} to keep insert order.
 * <p>
 * <b>WARNING</b>: This is just enough for the usage in this maven plugin.
 *
 * @author Ralf Schandl
 */
public class OrderedProperties extends Properties {

    private static final long serialVersionUID = 1L;

    /** Keyset preserving insert order. */
    private final HashSet<Object> keySet = new LinkedHashSet<>();

    @Override
    public synchronized Object put(final Object key, final Object value) {
        keySet.add(key);
        return super.put(key, value);
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(keySet);
    }

    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet(keySet);
    }

    @Override
    public Set<java.util.Map.Entry<Object, Object>> entrySet() {
        throw new NotImplementedException("entrySet() not implemented. Use keySet()");
    }

    // Will never be used in our scenario
    @Override
    public synchronized void clear() {
        keySet.clear();
        super.clear();
    }

    // Will never be used in our scenario
    @Override
    public synchronized Object remove(final Object key) {
        keySet.remove(key);
        return super.remove(key);
    }

    // Will never be used in our scenario
    @Override
    public synchronized boolean remove(final Object key, final Object value) {
        if (super.remove(key, value)) {
            keySet.remove(key);
            return true;
        } else {
            return false;
        }
    }

}
