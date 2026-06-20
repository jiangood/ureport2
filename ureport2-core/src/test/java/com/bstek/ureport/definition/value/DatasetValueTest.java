package com.bstek.ureport.definition.value;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DatasetValueTest {

    @Test
    public void testGetType() {
        DatasetValue dv = new DatasetValue();
        assertEquals(ValueType.dataset, dv.getType());
    }

    @Test
    public void testGetValue() {
        DatasetValue dv = new DatasetValue();
        dv.setDatasetName("ds1");
        dv.setAggregate(AggregateType.sum);
        dv.setProperty("amount");
        assertEquals("ds1.sum(amount)", dv.getValue());
    }

    @Test
    public void testGetValueNullProperty() {
        DatasetValue dv = new DatasetValue();
        dv.setDatasetName("ds2");
        dv.setAggregate(AggregateType.count);
        assertEquals("ds2.count()", dv.getValue());
    }

    @Test
    public void testGetValueLongPropertyTruncated() {
        DatasetValue dv = new DatasetValue();
        dv.setDatasetName("ds3");
        dv.setAggregate(AggregateType.avg);
        dv.setProperty("thisIsAVeryLongPropertyName");
        String val = dv.getValue();
        assertTrue(val.startsWith("ds3.avg("));
        assertTrue(val.contains("..."));
        assertTrue(val.endsWith(")"));
        assertFalse(val.contains("thisIsAVeryLongPropertyName"));
    }

    @Test
    public void testAggregateType() {
        assertEquals("sum", AggregateType.sum.name());
        assertEquals("avg", AggregateType.avg.name());
        assertEquals("count", AggregateType.count.name());
        assertEquals("customgroup", AggregateType.customgroup.name());
    }
}
