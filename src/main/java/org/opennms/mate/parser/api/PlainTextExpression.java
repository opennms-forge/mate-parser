package org.opennms.mate.parser.api;

import java.util.Objects;

public class PlainTextExpression implements Expression {
    private final String content;

    private PlainTextExpression(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "PlainTextExpression{" +
                "content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlainTextExpression that = (PlainTextExpression) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    static PlainTextExpression of(final String content) {
        return new PlainTextExpression(content);
    }

    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
}
