package com.bstek.ureport.definition;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.build.paging.HeaderFooter;

public class HeaderFooterDefinitionTest {

    @Test
    public void testDefaultValues() {
        HeaderFooterDefinition hf = new HeaderFooterDefinition();
        assertNull(hf.getLeft());
        assertNull(hf.getCenter());
        assertNull(hf.getRight());
        assertEquals("宋体", hf.getFontFamily());
        assertEquals(10, hf.getFontSize());
        assertEquals("0,0,0", hf.getForecolor());
        assertFalse(hf.isBold());
        assertFalse(hf.isItalic());
        assertFalse(hf.isUnderline());
        assertEquals(30, hf.getHeight());
        assertEquals(30, hf.getMargin());
        assertNull(hf.getLeftExpression());
        assertNull(hf.getCenterExpression());
        assertNull(hf.getRightExpression());
    }

    @Test
    public void testGettersSetters() {
        HeaderFooterDefinition hf = new HeaderFooterDefinition();
        hf.setLeft("left-text");
        hf.setCenter("center-text");
        hf.setRight("right-text");
        hf.setFontFamily("Arial");
        hf.setFontSize(12);
        hf.setForecolor("255,0,0");
        hf.setBold(true);
        hf.setItalic(true);
        hf.setUnderline(true);
        hf.setHeight(40);
        hf.setMargin(20);

        assertEquals("left-text", hf.getLeft());
        assertEquals("center-text", hf.getCenter());
        assertEquals("right-text", hf.getRight());
        assertEquals("Arial", hf.getFontFamily());
        assertEquals(12, hf.getFontSize());
        assertEquals("255,0,0", hf.getForecolor());
        assertTrue(hf.isBold());
        assertTrue(hf.isItalic());
        assertTrue(hf.isUnderline());
        assertEquals(40, hf.getHeight());
        assertEquals(20, hf.getMargin());
    }


}
