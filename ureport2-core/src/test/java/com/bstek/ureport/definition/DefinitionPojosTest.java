package com.bstek.ureport.definition;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import com.bstek.ureport.definition.value.Value;

public class DefinitionPojosTest {

    @Test
    public void testPaperSize() {
        PaperSize ps = new PaperSize(595, 842);
        assertEquals(595, ps.getWidth());
        assertEquals(842, ps.getHeight());
    }

    @Test
    public void testPaperTypeGetPaperSize() {
        PaperSize a4 = PaperType.A4.getPaperSize();
        assertEquals(595, a4.getWidth());
        assertEquals(842, a4.getHeight());
    }

    @Test
    public void testPaperTypeCustomThrows() {
        assertThrows(com.bstek.ureport.exception.ReportComputeException.class,
            () -> PaperType.CUSTOM.getPaperSize());
    }

    @Test
    public void testConditionPropertyItemDefaults() {
        ConditionPropertyItem item = new ConditionPropertyItem();
        assertNull(item.getName());
        assertNull(item.getCondition());
        assertNull(item.getConditions());
        assertEquals(-1, item.getRowHeight());
        assertEquals(-1, item.getColWidth());
        assertNull(item.getNewValue());
        assertNull(item.getLinkUrl());
        assertNull(item.getCellStyle());
        assertNull(item.getExpression());
        assertNull(item.getExpr());
    }

    @Test
    public void testConditionPropertyItemSetters() {
        ConditionPropertyItem item = new ConditionPropertyItem();
        item.setName("testProperty");
        item.setRowHeight(30);
        item.setColWidth(100);
        item.setNewValue("new");
        item.setLinkUrl("http://example.com");
        item.setLinkTargetWindow("_blank");

        assertEquals("testProperty", item.getName());
        assertEquals(30, item.getRowHeight());
        assertEquals(100, item.getColWidth());
        assertEquals("new", item.getNewValue());
        assertEquals("http://example.com", item.getLinkUrl());
        assertEquals("_blank", item.getLinkTargetWindow());
    }

    @Test
    public void testConditionPropertyItemLinkParameters() {
        ConditionPropertyItem item = new ConditionPropertyItem();
        ArrayList<LinkParameter> params = new ArrayList<>();
        LinkParameter lp = new LinkParameter();
        lp.setName("id");
        lp.setValue("1");
        params.add(lp);
        item.setLinkParameters(params);
        assertSame(params, item.getLinkParameters());
    }

    @Test
    public void testConditionPropertyItemCellStyle() {
        ConditionPropertyItem item = new ConditionPropertyItem();
        ConditionCellStyle style = new ConditionCellStyle();
        item.setCellStyle(style);
        assertSame(style, item.getCellStyle());
    }

    @Test
    public void testConditionPropertyItemPaging() {
        ConditionPropertyItem item = new ConditionPropertyItem();
        ConditionPaging paging = new ConditionPaging();
        paging.setPosition(PagingPosition.after);
        item.setPaging(paging);
        assertSame(paging, item.getPaging());
    }

    @Test
    public void testLinkParameter() {
        LinkParameter lp = new LinkParameter();
        lp.setName("p1");
        lp.setValue("v1");
        assertEquals("p1", lp.getName());
        assertEquals("v1", lp.getValue());
        assertNull(lp.getValueExpression());
    }

    @Test
    public void testPagingPosition() {
        assertEquals("before", PagingPosition.before.name());
        assertEquals("after", PagingPosition.after.name());
    }

    @Test
    public void testImageValueSourceSetter() {
        com.bstek.ureport.definition.value.ImageValue iv =
            new com.bstek.ureport.definition.value.ImageValue();
        iv.setSource(com.bstek.ureport.definition.value.Source.text);
        assertEquals(com.bstek.ureport.definition.value.Source.text, iv.getSource());
    }
}
