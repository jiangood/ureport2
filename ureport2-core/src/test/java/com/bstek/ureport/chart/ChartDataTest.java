package com.bstek.ureport.chart;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.model.Cell;

public class ChartDataTest {

    @Test
    public void testConstructor() {
        Cell cell = new Cell();
        cell.setName("chart1");

        ChartData data = new ChartData("{\"type\":\"bar\"}", cell);
        assertEquals("chart1", data.getId());
        assertEquals("{\"type\":\"bar\"}", data.getJson());
    }

    @Test
    public void testWidthHeight() {
        Cell cell = new Cell();
        cell.setName("c1");

        ChartData data = new ChartData("{}", cell);
        data.setWidth(800);
        data.setHeight(600);
        assertEquals(800, data.getWidth());
        assertEquals(600, data.getHeight());
    }

    @Test
    public void testBase64DataDirect() {
        Cell cell = new Cell();
        cell.setName("c1");

        ChartData data = new ChartData("{}", cell);
        data.setBase64Data("base64string");
        assertEquals("base64string", data.retriveBase64Data());
    }
}
