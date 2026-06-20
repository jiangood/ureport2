package com.bstek.ureport.chart.axes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.chart.axes.impl.XAxes;
import com.bstek.ureport.chart.axes.impl.YAxes;

public class AxesPojosTest {

    @Test
    public void testScaleLabelDefaults() {
        ScaleLabel label = new ScaleLabel();
        assertEquals("", label.getLabelString());
        assertFalse(label.isDisplay());
    }

    @Test
    public void testScaleLabelSetters() {
        ScaleLabel label = new ScaleLabel();
        label.setDisplay(false);
        label.setLabelString("Label");
        assertFalse(label.isDisplay());
        assertEquals("Label", label.getLabelString());
    }

    @Test
    public void testXAxesDefaults() {
        XAxes axes = new XAxes();
        assertEquals(XPosition.bottom, axes.getXposition());
        assertNull(axes.getScaleLabel());
    }

    @Test
    public void testXAxesSetters() {
        XAxes axes = new XAxes();
        axes.setXposition(XPosition.top);
        ScaleLabel label = new ScaleLabel();
        label.setLabelString("X Axis");
        axes.setScaleLabel(label);
        assertEquals(XPosition.top, axes.getXposition());
        assertSame(label, axes.getScaleLabel());
    }

    @Test
    public void testYAxesDefaults() {
        YAxes axes = new YAxes();
        assertNull(axes.getYposition());
        assertNull(axes.getScaleLabel());
    }

    @Test
    public void testYAxesSetters() {
        YAxes axes = new YAxes();
        axes.setYposition(YPosition.right);
        ScaleLabel label = new ScaleLabel();
        label.setLabelString("Y Axis");
        axes.setScaleLabel(label);
        assertEquals(YPosition.right, axes.getYposition());
        assertSame(label, axes.getScaleLabel());
    }

    @Test
    public void testXPosition() {
        assertEquals("bottom", XPosition.bottom.name());
        assertEquals("top", XPosition.top.name());
    }

    @Test
    public void testYPosition() {
        assertEquals("left", YPosition.left.name());
        assertEquals("right", YPosition.right.name());
    }
}
