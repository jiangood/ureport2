package com.bstek.ureport.expression.model.expr;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.NoneExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.expression.model.expr.set.SimpleValueSetExpression;

import java.math.BigDecimal;

public class ExpressionConstantTest {

    /* ===== BooleanExpression ===== */
    @Test
    public void testBooleanTrue() {
        BooleanExpression e = new BooleanExpression(true);
        ExpressionData<?> result = e.compute(null, null, null);
        assertTrue(result instanceof ObjectExpressionData);
        assertEquals(true, result.getData());
    }

    @Test
    public void testBooleanFalse() {
        BooleanExpression e = new BooleanExpression(false);
        ExpressionData<?> result = e.compute(null, null, null);
        assertEquals(false, result.getData());
    }

    /* ===== IntegerExpression ===== */
    @Test
    public void testIntegerPositive() {
        IntegerExpression e = new IntegerExpression(42);
        ExpressionData<?> result = e.compute(null, null, null);
        assertTrue(result instanceof ObjectExpressionData);
        assertEquals(42, result.getData());
    }

    @Test
    public void testIntegerNegative() {
        IntegerExpression e = new IntegerExpression(-7);
        assertEquals(-7, e.compute(null, null, null).getData());
    }

    @Test
    public void testIntegerZero() {
        IntegerExpression e = new IntegerExpression(0);
        assertEquals(0, e.compute(null, null, null).getData());
    }

    /* ===== NumberExpression ===== */
    @Test
    public void testNumber() {
        NumberExpression e = new NumberExpression(new BigDecimal("3.14"));
        ExpressionData<?> result = e.compute(null, null, null);
        assertTrue(result instanceof ObjectExpressionData);
        assertEquals(3.14f, result.getData());
    }

    @Test
    public void testNumberNegative() {
        NumberExpression e = new NumberExpression(new BigDecimal("-2.5"));
        assertEquals(-2.5f, e.compute(null, null, null).getData());
    }

    /* ===== StringExpression ===== */
    @Test
    public void testString() {
        StringExpression e = new StringExpression("hello");
        ExpressionData<?> result = e.compute(null, null, null);
        assertTrue(result instanceof ObjectExpressionData);
        assertEquals("hello", result.getData());
    }

    @Test
    public void testStringEmpty() {
        StringExpression e = new StringExpression("");
        assertEquals("", e.compute(null, null, null).getData());
    }

    @Test
    public void testStringNullValue() {
        StringExpression e = new StringExpression(null);
        assertNull(e.compute(null, null, null).getData());
    }

    /* ===== NullExpression ===== */
    @Test
    public void testNull() {
        NullExpression e = new NullExpression();
        ExpressionData<?> result = e.compute(null, null, null);
        assertTrue(result instanceof NoneExpressionData);
        assertNull(result.getData());
    }

    /* ===== SimpleValueSetExpression ===== */
    @Test
    public void testSimpleValueString() {
        SimpleValueSetExpression e = new SimpleValueSetExpression("test");
        ExpressionData<?> result = e.execute(null, null, null);
        assertTrue(result instanceof ObjectExpressionData);
        assertEquals("test", result.getData());
    }

    @Test
    public void testSimpleValueInteger() {
        SimpleValueSetExpression e = new SimpleValueSetExpression(99);
        assertEquals(99, e.execute(null, null, null).getData());
    }

    @Test
    public void testSimpleValueNull() {
        SimpleValueSetExpression e = new SimpleValueSetExpression(null);
        assertNull(e.execute(null, null, null).getData());
    }

    /* ===== BaseExpression.expr getter/setter ===== */
    @Test
    public void testBaseExpressionExpr() {
        StringExpression e = new StringExpression("hello");
        assertNull(e.getExpr());
        e.setExpr("test-expr");
        assertEquals("test-expr", e.getExpr());
    }
}
