package com.bstek.ureport.parser.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.bstek.ureport.definition.ColumnDefinition;

public class ColumnParserTest {

    private ColumnParser parser = new ColumnParser();

    private Element element(String xml) {
        try {
            return DocumentHelper.parseText(xml).getRootElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testParseBasic() {
        Element ele = element("<column col-number=\"1\" width=\"80\"/>");
        ColumnDefinition col = parser.parse(ele);
        assertEquals(1, col.getColumnNumber());
        assertEquals(80, col.getWidth());
        assertFalse(col.isHide());
    }

    @Test
    public void testParseWithHide() {
        Element ele = element("<column col-number=\"2\" width=\"100\" hide=\"true\"/>");
        ColumnDefinition col = parser.parse(ele);
        assertEquals(2, col.getColumnNumber());
        assertEquals(100, col.getWidth());
        assertTrue(col.isHide());
    }

    @Test
    public void testParseWithoutWidth() {
        Element ele = element("<column col-number=\"3\"/>");
        ColumnDefinition col = parser.parse(ele);
        assertEquals(3, col.getColumnNumber());
        assertEquals(0, col.getWidth());
    }
}
