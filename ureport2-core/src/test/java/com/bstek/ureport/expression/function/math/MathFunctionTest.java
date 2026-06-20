package com.bstek.ureport.expression.function.math;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.expression.model.data.ObjectListExpressionData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MathFunctionTest {

    /* ===== AbsFunction ===== */
    @Test
    public void testAbsPositive() {
        AbsFunction f = new AbsFunction();
        assertEquals("abs", f.name());
        assertEquals(5.0, (Double) f.execute(singleData(5), null, null));
    }

    @Test
    public void testAbsNegative() {
        AbsFunction f = new AbsFunction();
        assertEquals(5.0, (Double) f.execute(singleData(-5), null, null));
    }

    @Test
    public void testAbsZero() {
        AbsFunction f = new AbsFunction();
        assertEquals(0.0, (Double) f.execute(singleData(0), null, null));
    }

    /* ===== CeilFunction ===== */
    @Test
    public void testCeil() {
        CeilFunction f = new CeilFunction();
        assertEquals("ceil", f.name());
        assertEquals(4.0, (Double) f.execute(singleData(3.14), null, null));
        assertEquals(-3.0, (Double) f.execute(singleData(-3.14), null, null));
    }

    @Test
    public void testCeilWithPrecision() {
        CeilFunction f = new CeilFunction();
        List<ExpressionData<?>> list = buildList(new ObjectExpressionData(3.14), new ObjectExpressionData(1));
        assertEquals(4.0, (Double) f.execute(list, null, null));
    }

    /* ===== CosFunction ===== */
    @Test
    public void testCos() {
        CosFunction f = new CosFunction();
        assertEquals("cos", f.name());
        assertEquals(Math.cos(0), (Double) f.execute(singleData(0), null, null));
        assertEquals(Math.cos(Math.PI), (Double) f.execute(singleData(Math.PI), null, null), 1e-15);
    }

    /* ===== SinFunction ===== */
    @Test
    public void testSin() {
        SinFunction f = new SinFunction();
        assertEquals("sin", f.name());
        assertEquals(Math.sin(0), (Double) f.execute(singleData(0), null, null));
        assertEquals(Math.sin(Math.PI / 2), (Double) f.execute(singleData(Math.PI / 2), null, null), 1e-15);
    }

    /* ===== TanFunction ===== */
    @Test
    public void testTan() {
        TanFunction f = new TanFunction();
        assertEquals("tan", f.name());
        assertEquals(Math.tan(0), (Double) f.execute(singleData(0), null, null));
        assertEquals(Math.tan(Math.PI / 4), (Double) f.execute(singleData(Math.PI / 4), null, null), 1e-15);
    }

    /* ===== ExpFunction ===== */
    @Test
    public void testExp() {
        ExpFunction f = new ExpFunction();
        assertEquals("exp", f.name());
        assertEquals(Math.exp(0), (Double) f.execute(singleData(0), null, null));
        assertEquals(Math.exp(1), (Double) f.execute(singleData(1), null, null), 1e-15);
    }

    /* ===== FloorFunction ===== */
    @Test
    public void testFloor() {
        FloorFunction f = new FloorFunction();
        assertEquals("floor", f.name());
        assertEquals(3.0, (Double) f.execute(singleData(3.14), null, null));
        assertEquals(-4.0, (Double) f.execute(singleData(-3.14), null, null));
    }

    @Test
    public void testFloorWithPrecision() {
        FloorFunction f = new FloorFunction();
        List<ExpressionData<?>> list = buildList(new ObjectExpressionData(3.14), new ObjectExpressionData(1));
        assertEquals(3.0, (Double) f.execute(list, null, null));
    }

    /* ===== LogFunction ===== */
    @Test
    public void testLog() {
        LogFunction f = new LogFunction();
        assertEquals("log", f.name());
        assertEquals(Math.log(1), (Double) f.execute(singleData(1), null, null));
        assertEquals(Math.log(Math.E), (Double) f.execute(singleData(Math.E), null, null), 1e-15);
    }

    /* ===== Log10Function ===== */
    @Test
    public void testLog10() {
        Log10Function f = new Log10Function();
        assertEquals("log10", f.name());
        assertEquals(Math.log10(1), (Double) f.execute(singleData(1), null, null));
        assertEquals(Math.log10(100), (Double) f.execute(singleData(100), null, null), 1e-15);
    }

    /* ===== PowFunction ===== */
    @Test
    public void testPow() {
        PowFunction f = new PowFunction();
        assertEquals("pow", f.name());
        List<ExpressionData<?>> list = buildList(new ObjectExpressionData(2), new ObjectExpressionData(3));
        assertEquals(8.0, (Double) f.execute(list, null, null));
    }

    @Test
    public void testPowDefaultExponent() {
        PowFunction f = new PowFunction();
        assertEquals(1.0, (Double) f.execute(singleData(4), null, null));
    }

    /* ===== RoundFunction ===== */
    @Test
    public void testRoundDefault() {
        RoundFunction f = new RoundFunction();
        assertEquals("round", f.name());
        assertEquals(new BigDecimal("3"), f.execute(singleData(3.14), null, null));
    }

    @Test
    public void testRoundWithPrecision() {
        RoundFunction f = new RoundFunction();
        List<ExpressionData<?>> list = buildList(new ObjectExpressionData(3.14159), new ObjectExpressionData(2));
        assertEquals(new BigDecimal("3.14"), f.execute(list, null, null));
    }

    @Test
    public void testRoundWithNegativePrecision() {
        RoundFunction f = new RoundFunction();
        List<ExpressionData<?>> list = buildList(new ObjectExpressionData(314.159), new ObjectExpressionData(-1));
        BigDecimal result = (BigDecimal) f.execute(list, null, null);
        assertEquals(0, result.compareTo(new BigDecimal(310)));
    }

    /* ===== SqrtFunction ===== */
    @Test
    public void testSqrt() {
        SqrtFunction f = new SqrtFunction();
        assertEquals("sqrt", f.name());
        assertEquals(Math.sqrt(4), (Double) f.execute(singleData(4), null, null));
        assertEquals(Math.sqrt(2), (Double) f.execute(singleData(2), null, null), 1e-15);
    }

    /* ===== ChnFunction ===== */
    @Test
    public void testChnZero() {
        ChnFunction f = new ChnFunction();
        assertEquals("chn", f.name());
        assertEquals("零", f.execute(singleData(0), null, null));
    }

    @Test
    public void testChnPositive() {
        ChnFunction f = new ChnFunction();
        String result = (String) f.execute(singleData(30010.12), null, null);
        assertNotNull(result);
        assertTrue(result.contains("叁"));
        assertTrue(result.contains("万"));
    }

    /* ===== ChnMoneyFunction ===== */
    @Test
    public void testRmbZero() {
        ChnMoneyFunction f = new ChnMoneyFunction();
        assertEquals("rmb", f.name());
        assertEquals("零", f.execute(singleData(0), null, null));
    }

    @Test
    public void testRmbPositive() {
        ChnMoneyFunction f = new ChnMoneyFunction();
        String result = (String) f.execute(singleData(2031002101), null, null);
        assertNotNull(result);
    }

    /* ===== MedianFunction ===== */
    @Test
    public void testMedianSingleValue() {
        MedianFunction f = new MedianFunction();
        assertEquals("median", f.name());
        assertEquals(new BigDecimal(5), f.execute(listData(5), null, null));
    }

    @Test
    public void testMedianTwoValues() {
        MedianFunction f = new MedianFunction();
        BigDecimal result = (BigDecimal) f.execute(listData(2, 4), null, null);
        assertEquals(0, result.compareTo(new BigDecimal(3)));
    }

    @Test
    public void testMedianOddCount() {
        MedianFunction f = new MedianFunction();
        assertEquals(new BigDecimal(3), f.execute(listData(1, 3, 5), null, null));
    }

    @Test
    public void testMedianEvenCount() {
        MedianFunction f = new MedianFunction();
        BigDecimal result = (BigDecimal) f.execute(listData(1, 2, 4, 5), null, null);
        assertEquals(0, result.compareTo(new BigDecimal(3)));
    }

    /* ===== ModeFunction ===== */
    @Test
    public void testMode() {
        ModeFunction f = new ModeFunction();
        assertEquals("mode", f.name());
        assertEquals(new BigDecimal(2), f.execute(listData(1, 2, 2, 3), null, null));
    }

    @Test
    public void testModeSingleValue() {
        ModeFunction f = new ModeFunction();
        assertEquals(new BigDecimal(5), f.execute(listData(5), null, null));
    }

    /* ===== StdevpFunction ===== */
    @Test
    public void testStdevp() {
        StdevpFunction f = new StdevpFunction();
        assertEquals("stdevp", f.name());
        double result = (Double) f.execute(listData(2, 4, 4, 4, 5, 5, 7, 9), null, null);
        assertEquals(2.0, result, 0.0001);
    }

    /* ===== VaraFunction ===== */
    @Test
    public void testVara() {
        VaraFunction f = new VaraFunction();
        assertEquals("vara", f.name());
        BigDecimal result = (BigDecimal) f.execute(listData(2, 4, 4, 4, 5, 5, 7, 9), null, null);
        assertEquals(4.0, result.doubleValue(), 0.0001);
    }

    /* ===== RandomFunction ===== */
    @Test
    public void testRandom() {
        RandomFunction f = new RandomFunction();
        assertEquals("random", f.name());
        Object result = f.execute(new ArrayList<>(), null, null);
        assertTrue(result instanceof Double);
        double val = (Double) result;
        assertTrue(val >= 0 && val < 1, "random should be in [0,1) but was " + val);
    }

    @Test
    public void testRandomWithBound() {
        RandomFunction f = new RandomFunction();
        Object result = f.execute(singleData(100), null, null);
        assertTrue(result instanceof Integer, "result should be Integer but was " + result.getClass());
        int val = (Integer) result;
        assertTrue(val >= 0 && val < 100, "value should be in [0,100) but was " + val);
    }

    @Test
    public void testRandomWithZeroBound() {
        RandomFunction f = new RandomFunction();
        Object result = f.execute(singleData(0), null, null);
        assertTrue(result instanceof Double);
    }

    /* ===== helpers ===== */
    private List<ExpressionData<?>> singleData(Object value) {
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData(value));
        return list;
    }

    private List<ExpressionData<?>> listData(Object... values) {
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectListExpressionData(Arrays.asList(values)));
        return list;
    }

    @SafeVarargs
    private static <T> List<T> buildList(T... items) {
        List<T> list = new ArrayList<>();
        for (T item : items) {
            list.add(item);
        }
        return list;
    }
}
