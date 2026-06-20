package com.bstek.ureport.chart.dataset;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.chart.dataset.impl.category.SeriesType;

public class ChartDatasetPojosTest {

    @Test
    public void testBubbleDataConstructor() {
        BubbleData data = new BubbleData(1.5, 2.5, 3.0);
        assertEquals(1.5, data.getX(), 0.001);
        assertEquals(2.5, data.getY(), 0.001);
        assertEquals(3.0, data.getR(), 0.001);
    }

    @Test
    public void testBubbleDataSetters() {
        BubbleData data = new BubbleData(0, 0, 0);
        data.setX(10.5);
        data.setY(20.5);
        data.setR(5.0);
        assertEquals(10.5, data.getX(), 0.001);
        assertEquals(20.5, data.getY(), 0.001);
        assertEquals(5.0, data.getR(), 0.001);
    }

    @Test
    public void testScatterDataConstructor() {
        ScatterData data = new ScatterData(3.0, 4.0);
        assertEquals(3.0, data.getX(), 0.001);
        assertEquals(4.0, data.getY(), 0.001);
    }

    @Test
    public void testScatterDataSetters() {
        ScatterData data = new ScatterData(0, 0);
        data.setX(7.5);
        data.setY(8.5);
        assertEquals(7.5, data.getX(), 0.001);
        assertEquals(8.5, data.getY(), 0.001);
    }

    @Test
    public void testCollectType() {
        assertEquals("select", CollectType.select.name());
        assertEquals("count", CollectType.count.name());
        assertEquals("sum", CollectType.sum.name());
        assertEquals("avg", CollectType.avg.name());
        assertEquals("max", CollectType.max.name());
        assertEquals("min", CollectType.min.name());
    }

    @Test
    public void testSeriesType() {
        assertEquals("property", SeriesType.property.name());
        assertEquals("text", SeriesType.text.name());
    }
}
