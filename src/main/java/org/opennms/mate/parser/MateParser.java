package org.opennms.mate.parser;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;
import org.opennms.mate.parser.api.Expression;
import org.opennms.mate.parser.api.JexlExpression;
import org.opennms.mate.parser.api.MateExpression;
import org.opennms.mate.parser.api.MateParserException;
import org.opennms.mate.parser.api.PlainTextExpression;
import org.opennms.mate.parser.api.SimpleExpression;

/**
 * Mate Parser
 */
public class MateParser {

    @Argument(metaVar = "EXPRESSION", usage = "The expression to be parsed", required = true)
    String expression = null;

    private MateParser() {
    }

    private void execute(final String args[]) throws MateParserException {
        final ParserProperties parserProperties = ParserProperties.defaults();
        parserProperties.withUsageWidth(80);
        final CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println("Usage: mate-parser.sh <EXPRESSION>\n");
            new CmdLineParser(new MateParser()).printUsage(System.err);
            System.err.println();
            return;
        }

        final MateExpression mateExpression = MateExpression.of(expression);

        Expression.Visitor visitor = new Expression.Visitor() {
            @Override
            public void start() {
                System.out.println("--- Start of Mate expression ---");
            }

            @Override
            public void finish() {
                System.out.println("--- End of Mate expression ---");
            }

            @Override
            public void visit(PlainTextExpression nonMate) {
                System.out.println(nonMate);
            }

            @Override
            public void visit(JexlExpression jexlExpression) {
                System.out.println(jexlExpression);
            }

            @Override
            public void visit(SimpleExpression simpleExpression) {
                System.out.println(simpleExpression);
            }
        };

        Expression.visit(mateExpression.getExpressions(), visitor);
    }

    public static void main(String args[]) throws MateParserException {
        new MateParser().execute(args);
    }
}
