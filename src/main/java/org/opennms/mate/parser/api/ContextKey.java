package org.opennms.mate.parser.api;

import java.util.Objects;

public class ContextKey {
    private final String context;
    private final String key;

    private ContextKey(final String context, final String key) {
        this.context = context;
        this.key = key;
    }

    @Override
    public String toString() {
        return "ContextKey{" +
                "context='" + context + '\'' +
                ", key='" + key + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContextKey that = (ContextKey) o;
        return Objects.equals(context, that.context) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, key);
    }

    static ContextKey of(final String contextKey) {
        if (contextKey.contains(":")) {
            final int index = contextKey.indexOf(':');
            return new ContextKey(
                    contextKey.substring(0, index),
                    contextKey.substring(index + 1)
            );
        } else {
            return null;
        }
    }

    public String getContext() {
        return context;
    }

    public String getKey() {
        return key;
    }
}