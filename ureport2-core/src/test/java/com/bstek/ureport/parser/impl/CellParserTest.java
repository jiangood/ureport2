package com.bstek.ureport.parser.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.bstek.ureport.definition.CellDefinition;
import com.bstek.ureport.definition.Expand;
import com.bstek.ureport.definition.value.SimpleValue;

public class CellParserTest {

    private CellParser parser = new CellParser();

    private Element element(String xml) {
        try {
            return DocumentHelper.parseText(xml).getRootElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testParseBasic() {
        Element ele = element("<cell name=\"A1\" row=\"1\" col=\"1\"><simple-value>hello</simple-value></cell>");
        CellDefinition cell = parser.parse(ele);
        assertEquals("A1", cell.getName());
        assertEquals(1, cell.getRowNumber());
        assertEquals(1, cell.getColumnNumber());
        assertEquals(Expand.None, cell.getExpand());
        assertTrue(cell.getValue() instanceof SimpleValue);
        assertEquals("hello", ((SimpleValue) cell.getValue()).getValue());
    }

    @Test
    public void testParseWithExpand() {
        Element ele = element("<cell expand=\"Down\" name=\"B2\" row=\"2\" col=\"3\"><simple-value>data</simple-value></cell>");
        CellDefinition cell = parser.parse(ele);
        assertEquals("B2", cell.getName());
        assertEquals(2, cell.getRowNumber());
        assertEquals(3, cell.getColumnNumber());
        assertEquals(Expand.Down, cell.getExpand());
    }

    @Test
    public void testParseWithRowAndColSpan() {
        Element ele = element("<cell name=\"A1\" row=\"1\" col=\"1\" row-span=\"2\" col-span=\"3\"><simple-value>span</simple-value></cell>");
        CellDefinition cell = parser.parse(ele);
        assertEquals(2, cell.getRowSpan());
        assertEquals(3, cell.getColSpan());
    }

    @Test
    public void testParseWithLeftAndTopCell() {
        Element ele = element("<cell name=\"C3\" row=\"3\" col=\"3\" left-cell=\"B3\" top-cell=\"C2\"><simple-value>x</simple-value></cell>");
        CellDefinition cell = parser.parse(ele);
        assertEquals("B3", cell.getLeftParentCellName());
        assertEquals("C2", cell.getTopParentCellName());
    }

    @Test
    public void testParseWithFillBlankRows() {
        Element ele = element("<cell name=\"A1\" row=\"1\" col=\"1\" fill-blank-rows=\"true\" multiple=\"5\"><simple-value>x</simple-value></cell>");
        CellDefinition cell = parser.parse(ele);
        assertTrue(cell.isFillBlankRows());
        assertEquals(5, cell.getMultiple());
    }

    @Test
    public void testParseWithLinkUrl() {
        Element ele = element("<cell name=\"A1\" row=\"1\" col=\"1\" link-url=\"/report\" link-target-window=\"_blank\"><simple-value>x</simple-value></cell>");
        CellDefinition cell = parser.parse(ele);
        assertEquals("/report", cell.getLinkUrl());
        assertEquals("_blank", cell.getLinkTargetWindow());
    }

    @Test
    public void testParseThrowsOnMissingValue() {
        Element ele = element("<cell name=\"A1\" row=\"1\" col=\"1\"></cell>");
        assertThrows(Exception.class, () -> parser.parse(ele));
    }
}
