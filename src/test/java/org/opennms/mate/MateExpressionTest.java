package org.opennms.mate;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.opennms.mate.parser.api.MateExpression;
import org.opennms.mate.parser.api.MateParserException;
import org.opennms.mate.parser.api.Expression;
import org.opennms.mate.parser.api.JexlExpression;
import org.opennms.mate.parser.api.PlainTextExpression;
import org.opennms.mate.parser.api.SimpleExpression;

public class MateExpressionTest {
    @Test
    public void testExpressions() throws Exception {
        final MateExpression mateExpression = MateExpression.of("one-${ctx:key|ctx:key|default}-two-$[ctx:key|ctx:key|\"default\"]-three-$(3*mate('ctx:key|ctx:key|default')+mate('ctx:key|ctx:key|bar')*3)-four-$(3*mate('ctx:key|ctx:key|foo')+mate('ctx:key|ctx:key|default')*3)-five");
        for(Expression expression: mateExpression.getExpressions()) {
            System.out.println(expression);
        }

        assertEquals(9, mateExpression.getExpressions().size());
        assertTrue(mateExpression.getExpressions().get(0) instanceof PlainTextExpression);
        assertTrue(mateExpression.getExpressions().get(1) instanceof SimpleExpression);
        assertFalse(((SimpleExpression) mateExpression.getExpressions().get(1)).isRecursive());
        assertTrue(mateExpression.getExpressions().get(2) instanceof PlainTextExpression);
        assertTrue(mateExpression.getExpressions().get(3) instanceof SimpleExpression);
        assertTrue(((SimpleExpression) mateExpression.getExpressions().get(3)).isRecursive());
        assertTrue(mateExpression.getExpressions().get(4) instanceof PlainTextExpression);
        assertTrue(mateExpression.getExpressions().get(5) instanceof JexlExpression);
        assertTrue(mateExpression.getExpressions().get(6) instanceof PlainTextExpression);
        assertTrue(mateExpression.getExpressions().get(7) instanceof JexlExpression);
        assertTrue(mateExpression.getExpressions().get(8) instanceof PlainTextExpression);
    }

    @Test
    public void testColonInLiteral() throws Exception {
        final MateExpression mateExpression = MateExpression.of("one-${ctx:key|\"defa:ult}\"}");
        for(Expression expression: mateExpression.getExpressions()) {
            System.out.println(expression);
        }

        assertEquals(2, mateExpression.getExpressions().size());
        assertTrue(mateExpression.getExpressions().get(0) instanceof PlainTextExpression);
        assertTrue(mateExpression.getExpressions().get(1) instanceof SimpleExpression);
        assertFalse(((SimpleExpression) mateExpression.getExpressions().get(1)).isRecursive());
    }

    @Test
    public void testPipeInLiteral() throws Exception {
        final MateExpression mateExpression = MateExpression.of("one-${ctx:key|'defa|ult}'}");
        for(Expression expression: mateExpression.getExpressions()) {
            System.out.println(expression);
        }

        assertEquals(2, mateExpression.getExpressions().size());
        assertTrue(mateExpression.getExpressions().get(0) instanceof PlainTextExpression);
        assertTrue(mateExpression.getExpressions().get(1) instanceof SimpleExpression);
        assertFalse(((SimpleExpression) mateExpression.getExpressions().get(1)).isRecursive());
    }

    @Test
    public void testColonInKey() throws Exception {
        final MateExpression mateExpression = MateExpression.of("one-${ctx:key|ctx:damn:key|default}");
        for(Expression expression: mateExpression.getExpressions()) {
            System.out.println(expression);
        }

        assertEquals(2, mateExpression.getExpressions().size());
        assertTrue(mateExpression.getExpressions().get(0) instanceof PlainTextExpression);
        assertTrue(mateExpression.getExpressions().get(1) instanceof SimpleExpression);
        assertFalse(((SimpleExpression) mateExpression.getExpressions().get(1)).isRecursive());
    }

    @Test
    public void testQuotes() throws Exception {
        final MateExpression mateExpression = MateExpression.of("one-$(1+mate('ctx:key|ctx:key|\"foo\"')+2+mate('ctx:key|ctx:key|\'default\'')+3)-two");
        for(Expression expression: mateExpression.getExpressions()) {
            System.out.println(expression);
        }

        assertEquals(3, mateExpression.getExpressions().size());
        assertTrue(mateExpression.getExpressions().get(0) instanceof PlainTextExpression);
        assertTrue(mateExpression.getExpressions().get(1) instanceof JexlExpression);
        assertTrue(mateExpression.getExpressions().get(2) instanceof PlainTextExpression);
    }

    @Test
    public void testDefaultLiteral() throws Exception {
        final MateExpression mateExpression = MateExpression.of("one-${ctx:key|\"default containing a } bracket\"}-two");
        for(Expression expression: mateExpression.getExpressions()) {
            System.out.println(expression);
        }

        assertEquals(3, mateExpression.getExpressions().size());
        assertTrue(mateExpression.getExpressions().get(0) instanceof PlainTextExpression);
        assertTrue(mateExpression.getExpressions().get(1) instanceof SimpleExpression);
        assertFalse(((SimpleExpression) mateExpression.getExpressions().get(1)).isRecursive());
        assertTrue(mateExpression.getExpressions().get(2) instanceof PlainTextExpression);
    }

    @Test(expected = MateParserException.class)
    public void testIllegalQuotes() throws Exception {
        MateExpression.of("one-$(foo'bar)-two");
        fail();
    }
}
