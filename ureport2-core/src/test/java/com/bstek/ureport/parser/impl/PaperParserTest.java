package com.bstek.ureport.parser.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.bstek.ureport.definition.HtmlReportAlign;
import com.bstek.ureport.definition.Orientation;
import com.bstek.ureport.definition.PagingMode;
import com.bstek.ureport.definition.Paper;
import com.bstek.ureport.definition.PaperType;

public class PaperParserTest {

    private PaperParser parser = new PaperParser();

    private Element element(String xml) {
        try {
            return DocumentHelper.parseText(xml).getRootElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testParseA4Portrait() {
        Element ele = element("<paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\"></paper>");
        Paper paper = parser.parse(ele);
        assertEquals(PaperType.A4, paper.getPaperType());
        assertEquals(Orientation.portrait, paper.getOrientation());
        assertEquals(PagingMode.fitpage, paper.getPagingMode());
        assertEquals(595, paper.getWidth());
        assertEquals(842, paper.getHeight());
    }

    @Test
    public void testParseA4Landscape() {
        Element ele = element("<paper type=\"A4\" orientation=\"landscape\" paging-mode=\"fitpage\"></paper>");
        Paper paper = parser.parse(ele);
        assertEquals(Orientation.landscape, paper.getOrientation());
        assertEquals(595, paper.getWidth());
        assertEquals(842, paper.getHeight());
    }

    @Test
    public void testParseCustomSize() {
        Element ele = element("<paper type=\"CUSTOM\" orientation=\"portrait\" paging-mode=\"fitpage\" width=\"500\" height=\"400\"></paper>");
        Paper paper = parser.parse(ele);
        assertEquals(PaperType.CUSTOM, paper.getPaperType());
        assertEquals(500, paper.getWidth());
        assertEquals(400, paper.getHeight());
    }

    @Test
    public void testParseWithMargins() {
        Element ele = element("<paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\" left-margin=\"50\" right-margin=\"60\" top-margin=\"40\" bottom-margin=\"80\"></paper>");
        Paper paper = parser.parse(ele);
        assertEquals(50, paper.getLeftMargin());
        assertEquals(60, paper.getRightMargin());
        assertEquals(40, paper.getTopMargin());
        assertEquals(80, paper.getBottomMargin());
    }

    @Test
    public void testParseFixRows() {
        Element ele = element("<paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fixrows\" fixrows=\"25\"></paper>");
        Paper paper = parser.parse(ele);
        assertEquals(PagingMode.fixrows, paper.getPagingMode());
        assertEquals(25, paper.getFixRows());
    }

    @Test
    public void testParseColumnEnabled() {
        Element ele = element("<paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\" column-enabled=\"true\" column-count=\"3\" column-margin=\"15\"></paper>");
        Paper paper = parser.parse(ele);
        assertTrue(paper.isColumnEnabled());
        assertEquals(3, paper.getColumnCount());
        assertEquals(15, paper.getColumnMargin());
    }

    @Test
    public void testParseHtmlReportAlign() {
        Element ele = element("<paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\" html-report-align=\"center\"></paper>");
        Paper paper = parser.parse(ele);
        assertEquals(HtmlReportAlign.center, paper.getHtmlReportAlign());
    }

    @Test
    public void testParseHtmlIntervalRefresh() {
        Element ele = element("<paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\" html-interval-refresh-value=\"30\"></paper>");
        Paper paper = parser.parse(ele);
        assertEquals(30, paper.getHtmlIntervalRefreshValue());
    }

    @Test
    public void testParseBgImage() {
        Element ele = element("<paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\" bg-image=\"bg.png\"></paper>");
        Paper paper = parser.parse(ele);
        assertEquals("bg.png", paper.getBgImage());
    }
}
