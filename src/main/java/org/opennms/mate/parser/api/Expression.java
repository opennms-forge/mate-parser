package org.opennms.mate.parser.api;

import java.util.List;

public interface Expression {
    void accept(final Visitor visitor);

    interface Visitor {
        default void start() {}
        default void finish() {}
        default void visit(final PlainTextExpression plainTextExpression) {}
        default void visit(final JexlExpression jexlExpression) {}
        default void visit(final SimpleExpression simpleExpression) {}
    }

    static <V extends  Visitor> V visit(final List<Expression> elements, final V visitor) {
        visitor.start();

        for (final Expression element : elements) {
            element.accept(visitor);
        }

        visitor.finish();

        return visitor;
    }
}
