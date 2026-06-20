package com.bstek.ureport.expression.model.expr;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import com.bstek.ureport.expression.model.Operator;
import com.bstek.ureport.expression.model.expr.set.CellExpression;

public class ExpressionPojosTest {

    @Test
    public void testBaseExpression() {
        BaseExpression expr = new BaseExpression() {
            @Override
            protected com.bstek.ureport.expression.model.data.ExpressionData<?> compute(
                    com.bstek.ureport.model.Cell cell, com.bstek.ureport.model.Cell currentCell,
                    com.bstek.ureport.build.Context context) { return null; }
        };
        expr.setExpr("1+2");
        assertEquals("1+2", expr.getExpr());
    }

    @Test
    public void testIntegerExpression() {
        IntegerExpression expr = new IntegerExpression(42);
        assertNull(expr.getExpr());
    }

    @Test
    public void testNumberExpression() {
        NumberExpression expr = new NumberExpression(new BigDecimal("3.14"));
        assertNull(expr.getExpr());
    }

    @Test
    public void testStringExpression() {
        StringExpression expr = new StringExpression("hello");
        assertNull(expr.getExpr());
    }

    @Test
    public void testBooleanExpressionTrue() {
        BooleanExpression expr = new BooleanExpression(true);
        assertNull(expr.getExpr());
    }

    @Test
    public void testBooleanExpressionFalse() {
        BooleanExpression expr = new BooleanExpression(false);
        assertNull(expr.getExpr());
    }

    @Test
    public void testNullExpression() {
        NullExpression expr = new NullExpression();
        assertNull(expr.getExpr());
    }

    @Test
    public void testCellPositionExpressionSupportPaging() {
        CellPositionExpression expr = new CellPositionExpression("A1");
        assertFalse(expr.supportPaging());
    }

    @Test
    public void testCellPositionExpressionExtendsCellExpression() {
        CellPositionExpression expr = new CellPositionExpression("B2");
        assertTrue(expr instanceof CellExpression);
    }

    @Test
    public void testRelativeCellExpressionSupportPaging() {
        RelativeCellExpression expr = new RelativeCellExpression("A1");
        assertFalse(expr.supportPaging());
    }

    @Test
    public void testRelativeCellExpressionExtendsCellExpression() {
        RelativeCellExpression expr = new RelativeCellExpression("C3");
        assertTrue(expr instanceof CellExpression);
    }

    @Test
    public void testVariableExpression() {
        VariableExpression expr = new VariableExpression("myVar");
        assertNull(expr.getExpr());
    }

    @Test
    public void testParenExpression() {
        ParenExpression expr = new ParenExpression(
            new ArrayList<>(), new ArrayList<>());
        assertNotNull(expr.getExpressions());
        assertTrue(expr.getExpressions().isEmpty());
    }

    @Test
    public void testJoinExpression() {
        JoinExpression expr = new JoinExpression(
            Arrays.asList(Operator.Add, Operator.Subtract),
            Arrays.asList(new IntegerExpression(1), new IntegerExpression(2)));
        assertEquals(2, expr.getExpressions().size());
    }

    @Test
    public void testJoinExpressionSingleExpression() {
        JoinExpression expr = new JoinExpression(
            new ArrayList<>(), Arrays.asList(new IntegerExpression(5)));
        assertEquals(1, expr.getExpressions().size());
    }

    @Test
    public void testExpressionBlockDefaults() {
        ExpressionBlock block = new ExpressionBlock();
        assertNull(block.getExpressionList());
        assertNull(block.getReturnExpression());
    }

    @Test
    public void testExpressionBlockSetters() {
        ExpressionBlock block = new ExpressionBlock();
        block.setExpressionList(Arrays.asList(new IntegerExpression(1)));
        block.setReturnExpression(new StringExpression("done"));
        assertEquals(1, block.getExpressionList().size());
        assertNotNull(block.getReturnExpression());
    }

    @Test
    public void testFunctionExpressionDefaults() {
        FunctionExpression expr = new FunctionExpression();
        assertNull(expr.getName());
        assertNull(expr.getExpressions());
    }

    @Test
    public void testFunctionExpressionSetters() {
        FunctionExpression expr = new FunctionExpression();
        expr.setName("sum");
        expr.setExpressions(Arrays.asList(new IntegerExpression(10)));
        assertEquals("sum", expr.getName());
        assertEquals(1, expr.getExpressions().size());
    }
}
