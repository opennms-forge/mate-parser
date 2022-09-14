package org.opennms.mate.parser.api;

import java.util.Objects;

public class JexlExpression implements Expression {
    private final String content;

    private JexlExpression(String content) {
        this.content = content;
    }

    static JexlExpression of(final String content) {
        return new JexlExpression(content);
    }

    @Override
    public String toString() {
        return "JexlExpression{" +
                "content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JexlExpression that = (JexlExpression) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
}
