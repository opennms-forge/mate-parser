package org.opennms.mate.parser.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleExpression implements Expression {
    private final List<ContextKey> contextKeys;
    private final String defaultValue;

    private final boolean recursive;

    private SimpleExpression(final List<ContextKey> contextKeys, final String defaultValue, final boolean recursive) {
        this.contextKeys = contextKeys;
        this.defaultValue = defaultValue;
        this.recursive = recursive;
    }

    @Override
    public String toString() {
        return "SimpleExpression{" +
                "contextKeys=" + contextKeys +
                ", defaultValue='" + defaultValue + '\'' +
                ", recursive=" + recursive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleExpression that = (SimpleExpression) o;
        return recursive == that.recursive && Objects.equals(contextKeys, that.contextKeys) && Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextKeys, defaultValue, recursive);
    }

    public boolean isRecursive() {
        return recursive;
    }

    public List<ContextKey> getContextKeys() {
        return contextKeys;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    static SimpleExpression of(String expression, final boolean recursive) {
        final List<ContextKey> tokens = new ArrayList<>();

        String defaultValue = null;
        do {
            int index;

            if (expression.startsWith("\"")) {
                index = expression.indexOf("\"",1);
                final String string = expression.substring(1, index);
                defaultValue = string;
                break;
            } else if (expression.startsWith("'")) {
                index = expression.indexOf("'",1);
                final String string = expression.substring(1, index);
                defaultValue = string;
                break;
            } else {
                index = expression.indexOf("|");
                final String string;
                if (index == -1) {
                    string = expression;
                } else {
                    string = expression.substring(0, index);
                }
                final ContextKey token = ContextKey.of(string);
                if (token == null) {
                    defaultValue = string;
                } else {
                    tokens.add(token);
                }

                if (index == -1) {
                    break;
                }
            }

            expression = expression.substring(index + 1);
        } while (expression.length() > 0);

        return new SimpleExpression(tokens, defaultValue, recursive);
    }

    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
}
