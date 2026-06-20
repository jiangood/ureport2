package com.bstek.ureport.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.Range;
import com.bstek.ureport.definition.*;
import com.bstek.ureport.definition.value.SimpleValue;
import com.bstek.ureport.expression.model.Expression;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;

import java.text.SimpleDateFormat;
import java.util.*;

public class CellTest {

    @Test
    public void testDefaultValues() {
        Cell cell = new Cell();
        assertNull(cell.getName());
        assertEquals(0, cell.getRowSpan());
        assertEquals(0, cell.getColSpan());
        assertEquals(0, cell.getPageRowSpan());
        assertNull(cell.getData());
        assertNull(cell.getCellStyle());
        assertNull(cell.getCustomCellStyle());
        assertNull(cell.getValue());
        assertNull(cell.getRow());
        assertNull(cell.getColumn());
        assertNull(cell.getExpand());
        assertFalse(cell.isProcessed());
        assertFalse(cell.isBlankCell());
        assertFalse(cell.isExistPageFunction());
        assertNull(cell.getBindData());
        assertNull(cell.getDuplicateRange());
        assertFalse(cell.isForPaging());
        assertNull(cell.getLeftParentCell());
        assertNull(cell.getTopParentCell());
        assertNotNull(cell.getRowChildrenCellsMap());
        assertTrue(cell.getRowChildrenCellsMap().isEmpty());
        assertNotNull(cell.getColumnChildrenCellsMap());
        assertTrue(cell.getColumnChildrenCellsMap().isEmpty());
        assertNull(cell.getIncreaseSpanCellNames());
        assertNull(cell.getNewBlankCellsMap());
        assertNull(cell.getNewCellNames());
        assertNull(cell.getLinkUrl());
        assertNull(cell.getLinkTargetWindow());
        assertNull(cell.getLinkParameters());
        assertNull(cell.getLinkUrlExpression());
        assertFalse(cell.isFillBlankRows());
        assertEquals(0, cell.getMultiple());
        assertNull(cell.getConditionPropertyItems());
    }

    @Test
    public void testGettersSetters() {
        Cell cell = new Cell();
        cell.setName("A1");
        cell.setRowSpan(2);
        cell.setColSpan(3);
        cell.setPageRowSpan(1);
        cell.setData("test-data");
        cell.setFormatData("formatted");
        cell.setProcessed(true);
        cell.setBlankCell(true);
        cell.setExistPageFunction(true);
        cell.setForPaging(true);
        cell.setFillBlankRows(true);
        cell.setMultiple(5);
        cell.setRenderBean("bean1");

        CellStyle style = new CellStyle();
        cell.setCellStyle(style);
        CellStyle customStyle = new CellStyle();
        cell.setCustomCellStyle(customStyle);

        SimpleValue value = new SimpleValue("v");
        cell.setValue(value);

        List<Object> bindData = new ArrayList<>();
        cell.setBindData(bindData);

        Range range = new Range(1, 5);
        cell.setDuplicateRange(range);

        List<LinkParameter> params = new ArrayList<>();
        cell.setLinkParameters(params);
        cell.setLinkUrl("/url");
        cell.setLinkTargetWindow("_blank");

        List<ConditionPropertyItem> items = new ArrayList<>();
        cell.setConditionPropertyItems(items);

        Expression expr = (c, cc, ctx) -> new ObjectExpressionData("expr");
        cell.setLinkUrlExpression(expr);

        List<String> spanNames = new ArrayList<>();
        cell.setIncreaseSpanCellNames(spanNames);
        Map<String, BlankCellInfo> blankMap = new HashMap<>();
        cell.setNewBlankCellsMap(blankMap);
        List<String> newNames = new ArrayList<>();
        cell.setNewCellNames(newNames);

        assertEquals("A1", cell.getName());
        assertEquals(2, cell.getRowSpan());
        assertEquals(3, cell.getColSpan());
        assertEquals(1, cell.getPageRowSpan());
        assertEquals("test-data", cell.getData());
        assertEquals("formatted", cell.getFormatData());
        assertTrue(cell.isProcessed());
        assertTrue(cell.isBlankCell());
        assertTrue(cell.isExistPageFunction());
        assertTrue(cell.isForPaging());
        assertTrue(cell.isFillBlankRows());
        assertEquals(5, cell.getMultiple());
        assertEquals("bean1", cell.getRenderBean());
        assertSame(style, cell.getCellStyle());
        assertSame(customStyle, cell.getCustomCellStyle());
        assertSame(value, cell.getValue());
        assertSame(bindData, cell.getBindData());
        assertSame(range, cell.getDuplicateRange());
        assertSame(params, cell.getLinkParameters());
        assertEquals("/url", cell.getLinkUrl());
        assertEquals("_blank", cell.getLinkTargetWindow());
        assertSame(items, cell.getConditionPropertyItems());
        assertSame(expr, cell.getLinkUrlExpression());
        assertSame(spanNames, cell.getIncreaseSpanCellNames());
        assertSame(blankMap, cell.getNewBlankCellsMap());
        assertSame(newNames, cell.getNewCellNames());
    }

    @Test
    public void testNewCellFactoryMethod() {
        CellStyle style = new CellStyle();
        style.setFontSize(12);

        List<Row> rows = new ArrayList<>();
        Row row = new Row(rows);
        rows.add(row);
        row.setHeight(30);

        List<Column> columns = new ArrayList<>();
        Column column = new Column(columns);
        columns.add(column);
        column.setWidth(80);

        Cell parent = new Cell();
        parent.setName("P1");

        Cell original = new Cell();
        original.setName("A1");
        original.setRowSpan(2);
        original.setColSpan(3);
        original.setValue(new SimpleValue("hello"));
        original.setCellStyle(style);
        original.setRow(row);
        original.setColumn(column);
        original.setLeftParentCell(parent);
        original.setTopParentCell(parent);
        original.setExpand(Expand.Down);
        original.setPageRowSpan(1);
        original.setFillBlankRows(true);
        original.setMultiple(7);
        original.setLinkUrl("/test");
        original.setLinkTargetWindow("_self");

        Cell cloned = original.newCell();

        assertEquals("A1", cloned.getName());
        assertEquals(2, cloned.getRowSpan());
        assertEquals(3, cloned.getColSpan());
        assertEquals("hello", ((SimpleValue) cloned.getValue()).getValue());
        assertSame(style, cloned.getCellStyle());
        assertSame(row, cloned.getRow());
        assertSame(column, cloned.getColumn());
        assertSame(parent, cloned.getLeftParentCell());
        assertSame(parent, cloned.getTopParentCell());
        assertEquals(Expand.Down, cloned.getExpand());
        assertEquals(1, cloned.getPageRowSpan());
        assertTrue(cloned.isFillBlankRows());
        assertEquals(7, cloned.getMultiple());
        assertEquals("/test", cloned.getLinkUrl());
        assertEquals("_self", cloned.getLinkTargetWindow());
    }

    @Test
    public void testGetFormatDataReturnsDataWhenNull() {
        Cell cell = new Cell();
        cell.setData("raw");
        assertEquals("raw", cell.getFormatData());
    }

    @Test
    public void testGetFormatDataReturnsFormatDataWhenSet() {
        Cell cell = new Cell();
        cell.setData("raw");
        cell.setFormatData("formatted");
        assertEquals("formatted", cell.getFormatData());
    }

    @Test
    public void testDoFormatWithNoFormat() {
        Cell cell = new Cell();
        CellStyle style = new CellStyle();
        cell.setCellStyle(style);
        cell.setData(123.45);
        cell.doFormat();
        assertEquals(123.45, cell.getFormatData());
    }

    @Test
    public void testDoFormatWithDate() throws Exception {
        Cell cell = new Cell();
        CellStyle style = new CellStyle();
        style.setFormat("yyyy-MM-dd");
        cell.setCellStyle(style);
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2024-01-15");
        cell.setData(date);
        cell.doFormat();
        assertEquals("2024-01-15", cell.getFormatData());
    }

    @Test
    public void testDoFormatWithNumber() {
        Cell cell = new Cell();
        CellStyle style = new CellStyle();
        style.setFormat("#,##0.00");
        cell.setCellStyle(style);
        cell.setData(1234567.89);
        cell.doFormat();
        assertEquals("1,234,567.89", cell.getFormatData());
    }

    @Test
    public void testDoFormatWithInteger() {
        Cell cell = new Cell();
        CellStyle style = new CellStyle();
        style.setFormat("#,##0");
        cell.setCellStyle(style);
        cell.setData(1234);
        cell.doFormat();
        assertEquals("1,234", cell.getFormatData());
    }

    @Test
    public void testDoFormatWithNullData() {
        Cell cell = new Cell();
        CellStyle style = new CellStyle();
        style.setFormat("#,##0");
        cell.setCellStyle(style);
        cell.setData(null);
        cell.doFormat();
        assertNull(cell.getFormatData());
    }

    @Test
    public void testDoFormatWithNonNumericData() {
        Cell cell = new Cell();
        CellStyle style = new CellStyle();
        style.setFormat("#,##0");
        cell.setCellStyle(style);
        cell.setData("abc");
        cell.doFormat();
        assertEquals("abc", cell.getFormatData());
    }

    @Test
    public void testDoFormatWithCustomCellStyle() {
        Cell cell = new Cell();
        CellStyle style = new CellStyle();
        style.setFormat("#,##0.00");
        cell.setCellStyle(style);
        CellStyle custom = new CellStyle();
        custom.setFormat("yyyy/MM/dd");
        cell.setCustomCellStyle(custom);
        cell.setData(new Date(0));
        cell.doFormat();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        assertEquals(sdf.format(new Date(0)), cell.getFormatData());
    }

    @Test
    public void testGetPageRowSpanDefault() {
        Cell cell = new Cell();
        cell.setRowSpan(3);
        assertEquals(3, cell.getPageRowSpan());
    }

    @Test
    public void testGetPageRowSpanExplicit() {
        Cell cell = new Cell();
        cell.setRowSpan(3);
        cell.setPageRowSpan(1);
        assertEquals(1, cell.getPageRowSpan());
    }

    @Test
    public void testAddRowChild() {
        Cell parent = new Cell();
        parent.setName("P");

        Cell child = new Cell();
        child.setName("C");

        parent.addRowChild(child);
        Map<String, List<Cell>> childrenMap = parent.getRowChildrenCellsMap();
        assertEquals(1, childrenMap.size());
        assertTrue(childrenMap.containsKey("C"));
        assertEquals(1, childrenMap.get("C").size());
        assertSame(child, childrenMap.get("C").get(0));
    }

    @Test
    public void testAddRowChildNoDuplicate() {
        Cell parent = new Cell();
        parent.setName("P");

        Cell child = new Cell();
        child.setName("C");

        parent.addRowChild(child);
        parent.addRowChild(child);
        assertEquals(1, parent.getRowChildrenCellsMap().get("C").size());
    }

    @Test
    public void testAddRowChildPropagatesToLeftParent() {
        Cell grandparent = new Cell();
        grandparent.setName("GP");

        Cell parent = new Cell();
        parent.setName("P");
        parent.setLeftParentCell(grandparent);

        Cell child = new Cell();
        child.setName("C");

        parent.addRowChild(child);
        assertTrue(grandparent.getRowChildrenCellsMap().containsKey("C"));
    }

    @Test
    public void testAddColumnChild() {
        Cell parent = new Cell();
        parent.setName("P");

        Cell child = new Cell();
        child.setName("C");

        parent.addColumnChild(child);
        Map<String, List<Cell>> childrenMap = parent.getColumnChildrenCellsMap();
        assertEquals(1, childrenMap.size());
        assertTrue(childrenMap.containsKey("C"));
        assertEquals(1, childrenMap.get("C").size());
        assertSame(child, childrenMap.get("C").get(0));
    }

    @Test
    public void testAddColumnChildPropagatesToTopParent() {
        Cell grandparent = new Cell();
        grandparent.setName("GP");

        Cell parent = new Cell();
        parent.setName("P");
        parent.setTopParentCell(grandparent);

        Cell child = new Cell();
        child.setName("C");

        parent.addColumnChild(child);
        assertTrue(grandparent.getColumnChildrenCellsMap().containsKey("C"));
    }

    @Test
    public void testDoFormatWithEmptyStringData() {
        Cell cell = new Cell();
        CellStyle style = new CellStyle();
        style.setFormat("#,##0");
        cell.setCellStyle(style);
        cell.setData("");
        cell.doFormat();
        assertEquals("", cell.getFormatData());
    }
}
