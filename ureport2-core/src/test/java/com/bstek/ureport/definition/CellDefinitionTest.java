package com.bstek.ureport.definition;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.Range;
import com.bstek.ureport.definition.value.SimpleValue;
import com.bstek.ureport.definition.value.Value;
import com.bstek.ureport.expression.model.Expression;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.model.Cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellDefinitionTest {

    @Test
    public void testDefaultValues() {
        CellDefinition cd = new CellDefinition();
        assertEquals(0, cd.getRowNumber());
        assertEquals(0, cd.getColumnNumber());
        assertEquals(0, cd.getRowSpan());
        assertEquals(0, cd.getColSpan());
        assertNull(cd.getName());
        assertNull(cd.getValue());
        assertNotNull(cd.getCellStyle());
        assertNull(cd.getLinkUrl());
        assertNull(cd.getLinkTargetWindow());
        assertNull(cd.getLinkParameters());
        assertNull(cd.getLinkUrlExpression());
        assertFalse(cd.isFillBlankRows());
        assertEquals(0, cd.getMultiple());
        assertEquals(Expand.None, cd.getExpand());
        assertNull(cd.getDuplicateRange());
        assertNotNull(cd.getIncreaseSpanCellNames());
        assertTrue(cd.getIncreaseSpanCellNames().isEmpty());
        assertNotNull(cd.getNewBlankCellsMap());
        assertTrue(cd.getNewBlankCellsMap().isEmpty());
        assertNotNull(cd.getNewCellNames());
        assertTrue(cd.getNewCellNames().isEmpty());
        assertNull(cd.getLeftParentCellName());
        assertNull(cd.getTopParentCellName());
        assertNull(cd.getLeftParentCell());
        assertNull(cd.getTopParentCell());
        assertNotNull(cd.getRowChildrenCells());
        assertTrue(cd.getRowChildrenCells().isEmpty());
        assertNotNull(cd.getColumnChildrenCells());
        assertTrue(cd.getColumnChildrenCells().isEmpty());
        assertNull(cd.getConditionPropertyItems());
    }

    @Test
    public void testGettersSetters() {
        CellDefinition cd = new CellDefinition();
        cd.setRowNumber(3);
        cd.setColumnNumber(5);
        cd.setRowSpan(2);
        cd.setColSpan(4);
        cd.setName("A1");
        Value value = new SimpleValue("test");
        cd.setValue(value);
        cd.setLinkUrl("http://example.com");
        cd.setLinkTargetWindow("_blank");
        cd.setFillBlankRows(true);
        cd.setMultiple(10);
        cd.setExpand(Expand.Down);
        cd.setLeftParentCellName("B1");
        cd.setTopParentCellName("A2");

        CellDefinition leftParent = new CellDefinition();
        leftParent.setName("B1");
        cd.setLeftParentCell(leftParent);
        CellDefinition topParent = new CellDefinition();
        topParent.setName("A2");
        cd.setTopParentCell(topParent);

        CellStyle style = new CellStyle();
        style.setFontSize(16);
        cd.setCellStyle(style);

        Range range = new Range(1, 3);
        cd.setDuplicateRange(range);

        Expression expr = (cell, currentCell, context) -> new ObjectExpressionData("exprResult");
        cd.setLinkUrlExpression(expr);

        List<LinkParameter> params = new ArrayList<>();
        params.add(new LinkParameter());
        cd.setLinkParameters(params);

        List<ConditionPropertyItem> items = new ArrayList<>();
        items.add(new ConditionPropertyItem());
        cd.setConditionPropertyItems(items);

        assertEquals(3, cd.getRowNumber());
        assertEquals(5, cd.getColumnNumber());
        assertEquals(2, cd.getRowSpan());
        assertEquals(4, cd.getColSpan());
        assertEquals("A1", cd.getName());
        assertSame(value, cd.getValue());
        assertEquals("http://example.com", cd.getLinkUrl());
        assertEquals("_blank", cd.getLinkTargetWindow());
        assertTrue(cd.isFillBlankRows());
        assertEquals(10, cd.getMultiple());
        assertEquals(Expand.Down, cd.getExpand());
        assertEquals("B1", cd.getLeftParentCellName());
        assertEquals("A2", cd.getTopParentCellName());
        assertSame(leftParent, cd.getLeftParentCell());
        assertSame(topParent, cd.getTopParentCell());
        assertSame(style, cd.getCellStyle());
        assertSame(range, cd.getDuplicateRange());
        assertSame(expr, cd.getLinkUrlExpression());
        assertSame(params, cd.getLinkParameters());
        assertSame(items, cd.getConditionPropertyItems());
    }

    @Test
    public void testNewCellFactoryMethod() {
        CellDefinition cd = new CellDefinition();
        cd.setRowNumber(2);
        cd.setColumnNumber(3);
        cd.setRowSpan(1);
        cd.setColSpan(2);
        cd.setName("C4");
        SimpleValue value = new SimpleValue("hello");
        cd.setValue(value);
        cd.setExpand(Expand.Right);
        CellStyle cellStyle = new CellStyle();
        cellStyle.setFontSize(14);
        cd.setCellStyle(cellStyle);

        Map<String, BlankCellInfo> blankMap = new HashMap<>();
        blankMap.put("k1", new BlankCellInfo(1, 2, true));
        cd.getNewBlankCellsMap().putAll(blankMap);

        List<String> spanNames = new ArrayList<>();
        spanNames.add("span1");
        cd.getIncreaseSpanCellNames().addAll(spanNames);

        List<String> newNames = new ArrayList<>();
        newNames.add("new1");
        cd.getNewCellNames().addAll(newNames);

        Range range = new Range(2, 5);
        cd.setDuplicateRange(range);

        List<LinkParameter> linkParams = new ArrayList<>();
        LinkParameter lp = new LinkParameter();
        lp.setName("p1");
        lp.setValue("v1");
        linkParams.add(lp);
        cd.setLinkParameters(linkParams);

        cd.setLinkTargetWindow("_self");
        cd.setLinkUrl("/report");

        List<ConditionPropertyItem> items = new ArrayList<>();
        ConditionPropertyItem cpi = new ConditionPropertyItem();
        cpi.setName("cond1");
        items.add(cpi);
        cd.setConditionPropertyItems(items);

        cd.setFillBlankRows(true);
        cd.setMultiple(5);

        Expression expr = (cell, currentCell, context) -> new ObjectExpressionData("linkResult");
        cd.setLinkUrlExpression(expr);

        Cell cell = cd.newCell();

        assertSame(value, cell.getValue());
        assertEquals("C4", cell.getName());
        assertEquals(1, cell.getRowSpan());
        assertEquals(2, cell.getColSpan());
        assertEquals(Expand.Right, cell.getExpand());
        assertSame(cellStyle, cell.getCellStyle());
        assertEquals(blankMap, cell.getNewBlankCellsMap());
        assertEquals(spanNames, cell.getIncreaseSpanCellNames());
        assertEquals(newNames, cell.getNewCellNames());
        assertSame(range, cell.getDuplicateRange());
        assertSame(linkParams, cell.getLinkParameters());
        assertEquals("_self", cell.getLinkTargetWindow());
        assertEquals("/report", cell.getLinkUrl());
        assertSame(items, cell.getConditionPropertyItems());
        assertTrue(cell.isFillBlankRows());
        assertEquals(5, cell.getMultiple());
        assertSame(expr, cell.getLinkUrlExpression());
    }

    @Test
    public void testParentCellReferences() {
        CellDefinition cd = new CellDefinition();
        CellDefinition left = new CellDefinition();
        left.setName("leftCell");
        CellDefinition top = new CellDefinition();
        top.setName("topCell");

        cd.setLeftParentCell(left);
        cd.setTopParentCell(top);

        assertSame(left, cd.getLeftParentCell());
        assertSame(top, cd.getTopParentCell());
    }

    @Test
    public void testRowAndColumnChildrenCells() {
        CellDefinition cd = new CellDefinition();
        CellDefinition child1 = new CellDefinition();
        CellDefinition child2 = new CellDefinition();

        cd.getRowChildrenCells().add(child1);
        cd.getColumnChildrenCells().add(child2);

        assertEquals(1, cd.getRowChildrenCells().size());
        assertSame(child1, cd.getRowChildrenCells().get(0));
        assertEquals(1, cd.getColumnChildrenCells().size());
        assertSame(child2, cd.getColumnChildrenCells().get(0));
    }

    @Test
    public void testLinkParameters() {
        CellDefinition cd = new CellDefinition();
        LinkParameter p1 = new LinkParameter();
        p1.setName("a");
        LinkParameter p2 = new LinkParameter();
        p2.setName("b");

        List<LinkParameter> params = new ArrayList<>();
        params.add(p1);
        params.add(p2);
        cd.setLinkParameters(params);

        assertEquals(2, cd.getLinkParameters().size());
        assertSame(p1, cd.getLinkParameters().get(0));
        assertSame(p2, cd.getLinkParameters().get(1));
    }

    @Test
    public void testConditionPropertyItems() {
        CellDefinition cd = new CellDefinition();
        ConditionPropertyItem item1 = new ConditionPropertyItem();
        item1.setRowHeight(30);
        ConditionPropertyItem item2 = new ConditionPropertyItem();
        item2.setColWidth(100);

        List<ConditionPropertyItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        cd.setConditionPropertyItems(items);

        assertEquals(2, cd.getConditionPropertyItems().size());
        assertSame(item1, cd.getConditionPropertyItems().get(0));
        assertSame(item2, cd.getConditionPropertyItems().get(1));
    }

}
