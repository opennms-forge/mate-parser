package org.opennms.mate.parser.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MateExpression {

    private List<Expression> expressions = new ArrayList<>();

    private enum State {
        NON_MATE(),
        DOLLAR('$'),
        SIMPLE('{', '}'),
        REC('[', ']'),
        JEXL('(', ')'),
        DOUBLE('"'),
        SINGLE('\'');

        private Character startCharacter = null, endCharacter = null;
        private Character delimiter = null;

        State() {
        }

        State(final Character delimiter) {
            this.delimiter = delimiter;
        }

        State(final Character startCharacter, final Character endCharacter, final Character delimiter) {
            this.startCharacter = startCharacter;
            this.endCharacter = endCharacter;
            this.delimiter = delimiter;
        }

        State(final Character startCharacter, final Character endCharacter) {
            this.startCharacter = startCharacter;
            this.endCharacter = endCharacter;
        }

        public Character getDelimiter() {
            return delimiter;
        }

        public Character getStartCharacter() {
            return startCharacter;
        }

        public Character getEndCharacter() {
            return endCharacter;
        }
    }

    private MateExpression(final String expression) throws MateParserException {
        final StringBuffer stringBuffer = new StringBuffer();
        State state = State.NON_MATE;
        int brackets = 0;
        boolean escaped = false;
        State oldState = null;

        for (int column = 0; column < expression.length(); ++column) {
            final char c = expression.charAt(column);

            switch (state) {
                case NON_MATE:
                    if (c == '$') {
                        expressions.add(PlainTextExpression.of(stringBuffer.toString()));
                        stringBuffer.delete(0, Integer.MAX_VALUE);
                        state = State.DOLLAR;
                    } else {
                        stringBuffer.append(c);
                    }
                    break;
                case DOLLAR:
                    if (c == State.SIMPLE.getStartCharacter()) {
                        brackets = 0;
                        state = State.SIMPLE;
                        continue;
                    } else if (c == State.REC.getStartCharacter()) {
                        state = State.REC;
                        continue;
                    } else if (c == State.JEXL.getStartCharacter()) {
                        state = State.JEXL;
                        continue;
                    } else if (c == State.DOLLAR.getDelimiter()) {
                        stringBuffer.append(c);
                    } else {
                        throw new MateParserException("Unknown escape sequence: '$" + c + "' at position " + column);
                    }
                    break;
                case SIMPLE:
                case REC:
                case JEXL:
                    if (c == state.getEndCharacter()) {
                        if (brackets > 0) {
                            stringBuffer.append(c);
                            brackets--;
                        } else {
                            switch (state) {
                                case SIMPLE:
                                    expressions.add(SimpleExpression.of(stringBuffer.toString(), false));
                                    break;
                                case REC:
                                    expressions.add(SimpleExpression.of(stringBuffer.toString(), true));
                                    break;
                                case JEXL:
                                    expressions.add(JexlExpression.of(stringBuffer.toString()));
                                    break;
                            }
                            stringBuffer.delete(0, Integer.MAX_VALUE);
                            state = State.NON_MATE;
                        }
                    } else if (c == state.getStartCharacter()) {
                        brackets++;
                        stringBuffer.append(c);
                    } else if (c == State.DOUBLE.getDelimiter()) {
                        stringBuffer.append(c);
                        oldState = state;
                        state = State.DOUBLE;
                    } else if (c == State.SINGLE.getDelimiter()) {
                        stringBuffer.append(c);
                        oldState = state;
                        state = State.SINGLE;
                    } else {
                        stringBuffer.append(c);
                    }
                    break;
                case DOUBLE:
                case SINGLE:
                    if (c == '\\') {
                        escaped = true;
                    } else if (c == state.getDelimiter() && !escaped) {
                        state = oldState;
                    } else {
                        escaped = false;
                    }
                    stringBuffer.append(c);
                    break;
                default:
                    // this should never occur
                    throw new MateParserException("Unknown parser state " + state + " at position " + column);
            }
        }

        if (stringBuffer.length() > 0) {
            expressions.add(PlainTextExpression.of(stringBuffer.toString()));
        }

        if (state.equals(State.SINGLE)) {
            throw new MateParserException("Missing single quote");
        } else if(state.equals(State.DOUBLE)) {
            throw new MateParserException("Missing double quote");
        } else if (!state.equals(State.NON_MATE)) {
            throw new MateParserException("Unexpected end of input");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MateExpression that = (MateExpression) o;
        return Objects.equals(expressions, that.expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expressions);
    }

    @Override
    public String toString() {
        return "MateExpression{" +
                "expressions=" + expressions +
                '}';
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public static MateExpression of(final String expression) throws MateParserException {
        return new MateExpression(expression);
    }
}
