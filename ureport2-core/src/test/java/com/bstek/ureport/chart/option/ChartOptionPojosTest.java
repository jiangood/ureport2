package com.bstek.ureport.chart.option;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.chart.FontStyle;
import com.bstek.ureport.chart.option.impl.*;
import com.bstek.ureport.chart.plugins.DataLabelsPlugin;

public class ChartOptionPojosTest {

    @Test
    public void testAnimationsOptionDefaults() {
        AnimationsOption opt = new AnimationsOption();
        assertEquals(1000, opt.getDuration());
        assertEquals(Easing.easeOutQuart, opt.getEasing());
        assertEquals("animation", opt.getType());
    }

    @Test
    public void testAnimationsOptionSetters() {
        AnimationsOption opt = new AnimationsOption();
        opt.setDuration(500);
        opt.setEasing(Easing.easeInOutElastic);
        assertEquals(500, opt.getDuration());
        assertEquals(Easing.easeInOutElastic, opt.getEasing());
    }

    @Test
    public void testAnimationsOptionBuildJson() {
        AnimationsOption opt = new AnimationsOption();
        String json = opt.buildOptionJson();
        assertTrue(json.contains("\"duration\":1000"));
        assertTrue(json.contains("\"easing\":\"easeOutQuart\""));
    }

    @Test
    public void testLayoutOptionDefaults() {
        LayoutOption opt = new LayoutOption();
        assertEquals("layout", opt.getType());
        assertNull(opt.getPadding());
    }

    @Test
    public void testLayoutOptionPadding() {
        LayoutOption opt = new LayoutOption();
        Padding padding = new Padding();
        padding.setLeft(10);
        padding.setRight(10);
        padding.setTop(5);
        padding.setBottom(5);
        opt.setPadding(padding);
        assertSame(padding, opt.getPadding());
    }

    @Test
    public void testLegendOptionDefaults() {
        LegendOption opt = new LegendOption();
        assertTrue(opt.isDisplay());
        assertEquals(Position.top, opt.getPosition());
        assertNull(opt.getLabels());
        assertEquals("legend", opt.getType());
    }

    @Test
    public void testLegendOptionSetters() {
        LegendOption opt = new LegendOption();
        opt.setDisplay(false);
        opt.setPosition(Position.bottom);
        assertEquals(Position.bottom, opt.getPosition());
        assertFalse(opt.isDisplay());
    }

    @Test
    public void testTitleOptionDefaults() {
        TitleOption opt = new TitleOption();
        assertFalse(opt.isDisplay());
        assertEquals(Position.top, opt.getPosition());
        assertEquals(14, opt.getFontSize());
        assertEquals("#666", opt.getFontColor());
        assertEquals(FontStyle.bold, opt.getFontStyle());
        assertEquals(10, opt.getPadding());
        assertNull(opt.getText());
        assertEquals("title", opt.getType());
    }

    @Test
    public void testTitleOptionSetters() {
        TitleOption opt = new TitleOption();
        opt.setDisplay(true);
        opt.setText("My Chart");
        opt.setFontSize(18);
        opt.setFontColor("#000");
        opt.setPosition(Position.bottom);
        assertEquals("My Chart", opt.getText());
        assertEquals(18, opt.getFontSize());
        assertEquals("#000", opt.getFontColor());
    }

    @Test
    public void testTooltipOptionDefaults() {
        TooltipOption opt = new TooltipOption();
        assertTrue(opt.isEnabled());
        assertEquals(12, opt.getTitleFontSize());
        assertEquals(FontStyle.bold, opt.getTitleFontStyle());
        assertEquals("#fff", opt.getTitleFontColor());
        assertEquals(12, opt.getBodyFontSize());
        assertEquals(FontStyle.normal, opt.getBodyFontStyle());
        assertEquals("#fff", opt.getBodyFontColor());
        assertEquals("tooltips", opt.getType());
    }

    @Test
    public void testTooltipOptionSetters() {
        TooltipOption opt = new TooltipOption();
        opt.setEnabled(false);
        opt.setTitleFontSize(14);
        opt.setTitleFontColor("#000");
        opt.setBodyFontStyle(FontStyle.bold);
        opt.setBodyFontColor("#333");
        assertFalse(opt.isEnabled());
        assertEquals(14, opt.getTitleFontSize());
        assertEquals("#000", opt.getTitleFontColor());
        assertEquals(FontStyle.bold, opt.getBodyFontStyle());
        assertEquals("#333", opt.getBodyFontColor());
    }

    @Test
    public void testPaddingDefaults() {
        Padding p = new Padding();
        assertEquals(0, p.getLeft());
        assertEquals(0, p.getRight());
        assertEquals(0, p.getTop());
        assertEquals(0, p.getBottom());
    }

    @Test
    public void testPaddingSetters() {
        Padding p = new Padding();
        p.setLeft(1);
        p.setRight(2);
        p.setTop(3);
        p.setBottom(4);
        assertEquals(1, p.getLeft());
        assertEquals(2, p.getRight());
        assertEquals(3, p.getTop());
        assertEquals(4, p.getBottom());
    }

    @Test
    public void testPaddingToJson() {
        Padding p = new Padding();
        p.setLeft(5);
        p.setRight(10);
        String json = p.toJson();
        assertTrue(json.contains("left:5"));
        assertTrue(json.contains("right:10"));
    }

    @Test
    public void testLabelsDefaults() {
        Labels labels = new Labels();
        assertEquals(40, labels.getBoxWidth());
        assertEquals(12, labels.getFontSize());
        assertEquals(FontStyle.normal, labels.getFontStyle());
        assertEquals("#666", labels.getFontColor());
        assertEquals(10, labels.getPadding());
    }

    @Test
    public void testLabelsSetters() {
        Labels labels = new Labels();
        labels.setBoxWidth(50);
        labels.setFontSize(14);
        labels.setFontColor("#000");
        assertEquals(50, labels.getBoxWidth());
        assertEquals(14, labels.getFontSize());
        assertEquals("#000", labels.getFontColor());
    }

    @Test
    public void testLabelsToJson() {
        Labels labels = new Labels();
        String json = labels.toJson();
        assertTrue(json.contains("boxWidth:40"));
        assertTrue(json.contains("fontSize:12"));
    }

    @Test
    public void testLabelPositionEnum() {
        assertEquals("top", Position.top.name());
        assertEquals("bottom", Position.bottom.name());
        assertEquals("left", Position.left.name());
        assertEquals("right", Position.right.name());
    }

    @Test
    public void testEasingEnum() {
        assertEquals("easeOutQuart", Easing.easeOutQuart.name());
        assertEquals("easeInOutElastic", Easing.easeInOutElastic.name());
        assertEquals("linear", Easing.linear.name());
    }

    @Test
    public void testFontStyleEnum() {
        assertEquals("normal", FontStyle.normal.name());
        assertEquals("bold", FontStyle.bold.name());
        assertEquals("italic", FontStyle.italic.name());
    }

    @Test
    public void testDataLabelsPlugin() {
        DataLabelsPlugin plugin = new DataLabelsPlugin();
        assertEquals("data-labels", plugin.getName());
        assertFalse(plugin.isDisplay());
    }

    @Test
    public void testDataLabelsPluginSetDisplay() {
        DataLabelsPlugin plugin = new DataLabelsPlugin();
        plugin.setDisplay(true);
        assertTrue(plugin.isDisplay());
    }

    @Test
    public void testDataLabelsPluginToJson() {
        DataLabelsPlugin plugin = new DataLabelsPlugin();
        String json = plugin.toJson("bar");
        assertTrue(json.contains("\"datalabels\""));
        assertTrue(json.contains("\"display\":false"));
        plugin.setDisplay(true);
        json = plugin.toJson("bar");
        assertTrue(json.contains("\"display\":true"));
    }
}
