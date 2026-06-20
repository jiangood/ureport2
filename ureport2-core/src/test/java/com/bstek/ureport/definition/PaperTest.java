package com.bstek.ureport.definition;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PaperTest {

    @Test
    public void testDefaultValues() {
        Paper paper = new Paper();
        assertEquals(90, paper.getLeftMargin());
        assertEquals(90, paper.getRightMargin());
        assertEquals(72, paper.getTopMargin());
        assertEquals(72, paper.getBottomMargin());
        assertEquals(HtmlReportAlign.left, paper.getHtmlReportAlign());
        assertFalse(paper.isColumnEnabled());
        assertEquals(2, paper.getColumnCount());
        assertEquals(5, paper.getColumnMargin());
        assertEquals(0, paper.getHtmlIntervalRefreshValue());
    }

    @Test
    public void testSetAndGet() {
        Paper paper = new Paper();
        paper.setLeftMargin(50);
        paper.setRightMargin(60);
        paper.setTopMargin(40);
        paper.setBottomMargin(80);
        paper.setPaperType(PaperType.A4);
        paper.setPagingMode(PagingMode.fitpage);
        paper.setFixRows(20);
        paper.setWidth(800);
        paper.setHeight(600);
        paper.setOrientation(Orientation.landscape);
        paper.setHtmlReportAlign(HtmlReportAlign.center);
        paper.setBgImage("bg.png");
        paper.setColumnEnabled(true);
        paper.setColumnCount(3);
        paper.setColumnMargin(10);
        paper.setHtmlIntervalRefreshValue(30);

        assertEquals(50, paper.getLeftMargin());
        assertEquals(60, paper.getRightMargin());
        assertEquals(40, paper.getTopMargin());
        assertEquals(80, paper.getBottomMargin());
        assertEquals(PaperType.A4, paper.getPaperType());
        assertEquals(PagingMode.fitpage, paper.getPagingMode());
        assertEquals(20, paper.getFixRows());
        assertEquals(800, paper.getWidth());
        assertEquals(600, paper.getHeight());
        assertEquals(Orientation.landscape, paper.getOrientation());
        assertEquals(HtmlReportAlign.center, paper.getHtmlReportAlign());
        assertEquals("bg.png", paper.getBgImage());
        assertTrue(paper.isColumnEnabled());
        assertEquals(3, paper.getColumnCount());
        assertEquals(10, paper.getColumnMargin());
        assertEquals(30, paper.getHtmlIntervalRefreshValue());
    }
}
