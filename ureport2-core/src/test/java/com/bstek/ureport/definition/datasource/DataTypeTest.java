package com.bstek.ureport.definition.datasource;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

public class DataTypeTest {

    @Test
    public void testIntegerParse() {
        assertEquals(123, DataType.Integer.parse(123));
        assertEquals(456, DataType.Integer.parse("456"));
        assertNull(DataType.Integer.parse(""));
    }

    @Test
    public void testFloatParse() {
        assertEquals(1.5f, DataType.Float.parse(1.5f));
        assertEquals(3.14, (Double) DataType.Float.parse("3.14"), 0.001);
        assertNull(DataType.Float.parse(""));
    }

    @Test
    public void testBooleanParse() {
        assertTrue((Boolean) DataType.Boolean.parse(true));
        assertTrue((Boolean) DataType.Boolean.parse("true"));
        assertNull(DataType.Boolean.parse(""));
    }

    @Test
    public void testStringParse() {
        assertEquals("hello", DataType.String.parse("hello"));
        assertEquals("123", DataType.String.parse(123));
    }

    @Test
    public void testDateParse() {
        Object result = DataType.Date.parse("2024-01-15");
        assertInstanceOf(Date.class, result);

        result = DataType.Date.parse("2024-01-15 10:30:00");
        assertInstanceOf(Date.class, result);

        assertNull(DataType.Date.parse(""));
    }

    @Test
    public void testListParse() {
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) DataType.List.parse("a,b,c");
        assertEquals(3, list.size());
        assertEquals("a", list.get(0));

        assertNull(DataType.List.parse(""));
    }

    @Test
    public void testNullParse() {
        assertNull(DataType.Integer.parse(null));
        assertNull(DataType.Float.parse(null));
        assertNull(DataType.Boolean.parse(null));
        assertNull(DataType.String.parse(null));
        assertNull(DataType.Date.parse(null));
        assertNull(DataType.List.parse(null));
    }
}
