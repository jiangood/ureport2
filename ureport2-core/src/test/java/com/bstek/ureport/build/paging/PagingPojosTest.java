package com.bstek.ureport.build.paging;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.model.Row;

import java.util.ArrayList;
import java.util.List;

public class PagingPojosTest {

    @Test
    public void testHeaderFooterDefaults() {
        HeaderFooter hf = new HeaderFooter();
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
    }

    @Test
    public void testHeaderFooterGettersSetters() {
        HeaderFooter hf = new HeaderFooter();
        hf.setLeft("L");
        hf.setCenter("C");
        hf.setRight("R");
        hf.setFontFamily("Arial");
        hf.setFontSize(12);
        hf.setForecolor("255,0,0");
        hf.setBold(true);
        hf.setItalic(true);
        hf.setUnderline(true);
        hf.setHeight(40);
        hf.setMargin(20);

        assertEquals("L", hf.getLeft());
        assertEquals("C", hf.getCenter());
        assertEquals("R", hf.getRight());
        assertEquals("Arial", hf.getFontFamily());
        assertEquals(12, hf.getFontSize());
        assertEquals("255,0,0", hf.getForecolor());
        assertTrue(hf.isBold());
        assertTrue(hf.isItalic());
        assertTrue(hf.isUnderline());
        assertEquals(40, hf.getHeight());
        assertEquals(20, hf.getMargin());
    }

    @Test
    public void testPageConstructor() {
        List<Row> rows = new ArrayList<>();
        Row row = new Row(rows);
        rows.add(row);

        List<com.bstek.ureport.model.Column> cols = new ArrayList<>();
        com.bstek.ureport.model.Column col = new com.bstek.ureport.model.Column(cols);
        cols.add(col);

        Page page = new Page(rows, cols);
        assertSame(rows, page.getRows());
        assertSame(cols, page.getColumns());
        assertNull(page.getHeader());
        assertNull(page.getFooter());
    }

    @Test
    public void testPageHeaderFooter() {
        List<Row> rows = new ArrayList<>();
        List<com.bstek.ureport.model.Column> cols = new ArrayList<>();
        Page page = new Page(rows, cols);

        HeaderFooter header = new HeaderFooter();
        header.setLeft("header-left");
        HeaderFooter footer = new HeaderFooter();
        footer.setRight("footer-right");

        page.setHeader(header);
        page.setFooter(footer);

        assertSame(header, page.getHeader());
        assertSame(footer, page.getFooter());
    }

    @Test
    public void testRepeatRowsConstructor() {
        List<Row> headerRows = new ArrayList<>();
        List<Row> footerRows = new ArrayList<>();
        RepeatRows rr = new RepeatRows(headerRows, footerRows);

        assertSame(headerRows, rr.getHeaderRepeatRows());
        assertSame(footerRows, rr.getFooterRepeatRows());
    }
}
