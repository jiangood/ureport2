package com.bstek.ureport.parser.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.bstek.ureport.definition.Band;
import com.bstek.ureport.definition.RowDefinition;

public class RowParserTest {

    private RowParser parser = new RowParser();

    private Element element(String xml) {
        try {
            return DocumentHelper.parseText(xml).getRootElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testParseBasic() {
        Element ele = element("<row row-number=\"1\" height=\"30\"/>");
        RowDefinition row = parser.parse(ele);
        assertEquals(1, row.getRowNumber());
        assertEquals(30, row.getHeight());
        assertNull(row.getBand());
    }

    @Test
    public void testParseWithBand() {
        Element ele = element("<row row-number=\"2\" height=\"40\" band=\"headerrepeat\"/>");
        RowDefinition row = parser.parse(ele);
        assertEquals(2, row.getRowNumber());
        assertEquals(40, row.getHeight());
        assertEquals(Band.headerrepeat, row.getBand());
    }

    @Test
    public void testParseWithoutHeight() {
        Element ele = element("<row row-number=\"3\"/>");
        RowDefinition row = parser.parse(ele);
        assertEquals(3, row.getRowNumber());
        assertEquals(0, row.getHeight());
    }
}
