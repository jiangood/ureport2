package com.bstek.ureport.definition;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.awt.Font;

public class CellStyleTest {

    @Test
    public void testDefaultValues() {
        CellStyle style = new CellStyle();
        assertEquals(0, style.getFontSize());
        assertNull(style.getBgcolor());
        assertNull(style.getForecolor());
        assertNull(style.getFontFamily());
        assertNull(style.getFormat());
        assertNull(style.getAlign());
        assertNull(style.getValign());
        assertNull(style.getBold());
        assertNull(style.getItalic());
        assertNull(style.getUnderline());
        assertNull(style.getWrapCompute());
        assertEquals(0.0f, style.getLineHeight());
    }

    @Test
    public void testSetAndGet() {
        CellStyle style = new CellStyle();
        style.setBgcolor("#FFFFFF");
        style.setForecolor("#000000");
        style.setFontSize(14);
        style.setFontFamily("Arial");
        style.setFormat("#,##0.00");
        style.setLineHeight(1.5f);
        style.setBold(true);
        style.setItalic(false);
        style.setUnderline(true);
        style.setWrapCompute(true);
        style.setAlign(Alignment.center);
        style.setValign(Alignment.middle);

        assertEquals("#FFFFFF", style.getBgcolor());
        assertEquals("#000000", style.getForecolor());
        assertEquals(14, style.getFontSize());
        assertEquals("Arial", style.getFontFamily());
        assertEquals("#,##0.00", style.getFormat());
        assertEquals(1.5f, style.getLineHeight());
        assertTrue(style.getBold());
        assertFalse(style.getItalic());
        assertTrue(style.getUnderline());
        assertTrue(style.getWrapCompute());
        assertEquals(Alignment.center, style.getAlign());
        assertEquals(Alignment.middle, style.getValign());
    }

    @Test
    public void testGetFontDefault() {
        CellStyle style = new CellStyle();
        Font font = style.getFont();
        assertNotNull(font);
    }

    @Test
    public void testGetFontWithBold() {
        CellStyle style = new CellStyle();
        style.setBold(true);
        style.setFontSize(12);
        Font font = style.getFont();
        assertTrue(font.isBold());
    }

    @Test
    public void testGetFontWithItalic() {
        CellStyle style = new CellStyle();
        style.setItalic(true);
        style.setFontSize(12);
        Font font = style.getFont();
        assertTrue(font.isItalic());
    }

    @Test
    public void testGetFontWithBoldAndItalic() {
        CellStyle style = new CellStyle();
        style.setBold(true);
        style.setItalic(true);
        style.setFontSize(12);
        Font font = style.getFont();
        assertTrue(font.isBold());
        assertTrue(font.isItalic());
    }

    @Test
    public void testGetFontCached() {
        CellStyle style = new CellStyle();
        style.setFontSize(12);
        Font font1 = style.getFont();
        Font font2 = style.getFont();
        assertSame(font1, font2);
    }

    @Test
    public void testBorderSetAndGet() {
        CellStyle style = new CellStyle();
        Border left = new Border();
        left.setWidth(1);
        left.setColor("#000");
        left.setStyle(BorderStyle.solid);

        Border right = new Border();
        right.setWidth(2);
        right.setColor("#FFF");
        right.setStyle(BorderStyle.dashed);

        style.setLeftBorder(left);
        style.setRightBorder(right);

        assertSame(left, style.getLeftBorder());
        assertSame(right, style.getRightBorder());
        assertEquals(1, style.getLeftBorder().getWidth());
        assertEquals("#000", style.getLeftBorder().getColor());
        assertEquals(BorderStyle.solid, style.getLeftBorder().getStyle());
        assertEquals(2, style.getRightBorder().getWidth());
    }
}
