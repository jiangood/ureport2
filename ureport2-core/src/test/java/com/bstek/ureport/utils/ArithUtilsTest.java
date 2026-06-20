package com.bstek.ureport.utils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class ArithUtilsTest {

    @Test
    public void testAdd() {
        assertEquals(new BigDecimal(5), ArithUtils.add(2, 3));
        assertEquals(new BigDecimal(0), ArithUtils.add(-2, 2));
        assertEquals(new BigDecimal(-5), ArithUtils.add(-2, -3));
        assertEquals(new BigDecimal(3.5), ArithUtils.add(1.5, 2.0));
        assertEquals(new BigDecimal("3.3"), ArithUtils.add("1.1", "2.2"));
    }

    @Test
    public void testSub() {
        assertEquals(new BigDecimal(-1), ArithUtils.sub(2, 3));
        assertEquals(new BigDecimal(5), ArithUtils.sub(2, -3));
        assertEquals(new BigDecimal(-5), ArithUtils.sub(-2, 3));
        assertEquals(new BigDecimal("1.1"), ArithUtils.sub("3.3", "2.2"));
    }

    @Test
    public void testMul() {
        assertEquals(6.0, ArithUtils.mul(2, 3));
        assertEquals(-6.0, ArithUtils.mul(2, -3));
        assertEquals(0.0, ArithUtils.mul(0, 5));
        assertEquals(6.0, ArithUtils.mul(-2, -3));
    }

    @Test
    public void testDiv() {
        assertEquals(2.0, ArithUtils.div(6, 3));
        assertEquals(1.5, ArithUtils.div(3, 2));
        assertEquals(-2.0, ArithUtils.div(-6, 3));
    }

    @Test
    public void testCom() {
        assertEquals(0.0, ArithUtils.com(6, 3));
        assertEquals(1.0, ArithUtils.com(7, 3));
        assertEquals(0.0, ArithUtils.com(0, 3));
    }

    @Test
    public void testWithNullAndBlank() {
        assertThrows(Exception.class, () -> ArithUtils.add(null, 1));
        assertThrows(Exception.class, () -> ArithUtils.add(1, null));
    }
}
