package com.bstek.ureport.expression.model.expr.el;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.bstek.ureport.expression.model.Operator;

import java.math.BigDecimal;

public class ElUnitTest {

    private ElUnit createUnit(Object left, Object right, Operator op) {
        ElUnit unit = new ElUnit();
        unit.setLeft(left);
        unit.setRight(right);
        unit.setOp(op);
        return unit;
    }

    @Test
    public void testAddIntegers() {
        ElUnit unit = createUnit(3, 5, Operator.Add);
        Object result = unit.compute();
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, ((BigDecimal) result).compareTo(new BigDecimal(8)));
    }

    @Test
    public void testAddDoubles() {
        ElUnit unit = createUnit(1.5, 2.5, Operator.Add);
        assertTrue(unit.compute() instanceof BigDecimal);
        BigDecimal result = (BigDecimal) unit.compute();
        assertEquals(0, result.compareTo(new BigDecimal("4.0")));
    }

    @Test
    public void testSubtract() {
        ElUnit unit = createUnit(10, 3, Operator.Subtract);
        BigDecimal result = (BigDecimal) unit.compute();
        assertEquals(0, result.compareTo(new BigDecimal(7)));
    }

    @Test
    public void testSubtractNegativeResult() {
        ElUnit unit = createUnit(3, 10, Operator.Subtract);
        BigDecimal result = (BigDecimal) unit.compute();
        assertEquals(0, result.compareTo(new BigDecimal(-7)));
    }

    @Test
    public void testMultiply() {
        ElUnit unit = createUnit(4, 5, Operator.Multiply);
        Object result = unit.compute();
        assertTrue(result instanceof Double);
        assertEquals(20.0, (Double) result, 0.0001);
    }

    @Test
    public void testMultiplyWithDouble() {
        ElUnit unit = createUnit(2.5, 4, Operator.Multiply);
        Object result = unit.compute();
        assertTrue(result instanceof Double);
        assertEquals(10.0, (Double) result, 0.0001);
    }

    @Test
    public void testDivide() {
        ElUnit unit = createUnit(10, 3, Operator.Divide);
        Object result = unit.compute();
        assertTrue(result instanceof Double);
        assertEquals(3.33333333, (Double) result, 0.0001);
    }

    @Test
    public void testDivideExact() {
        ElUnit unit = createUnit(10, 2, Operator.Divide);
        assertEquals(5.0, (Double) unit.compute(), 0.0001);
    }

    @Test
    public void testComplementation() {
        ElUnit unit = createUnit(10, 3, Operator.Complementation);
        assertEquals(1.0, (Double) unit.compute(), 0.0001);
    }

    @Test
    public void testComplementationNoRemainder() {
        ElUnit unit = createUnit(10, 5, Operator.Complementation);
        assertEquals(0.0, (Double) unit.compute(), 0.0001);
    }

    @Test
    public void testNestedRightElUnit() {
        ElUnit inner = createUnit(3, 4, Operator.Add);
        ElUnit outer = new ElUnit();
        outer.setLeft(10);
        outer.setRight(inner);
        outer.setOp(Operator.Multiply);
        Object result = outer.compute();
        assertTrue(result instanceof Double);
        assertEquals(70.0, (Double) result, 0.0001);
    }

    @Test
    public void testNextOpAndNextUnit() {
        ElUnit unit = createUnit(10, 3, Operator.Add);
        unit.setNextOp(Operator.Multiply);
        ElUnit next = createUnit(2, 4, Operator.Add);
        unit.setNextUnit(next);
        assertEquals(Operator.Multiply, unit.getNextOp());
        assertEquals(next, unit.getNextUnit());
    }
}
