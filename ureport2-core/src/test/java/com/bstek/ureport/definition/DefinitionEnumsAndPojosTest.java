package com.bstek.ureport.definition;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.definition.datasource.DatasourceType;
import com.bstek.ureport.definition.dataset.Field;
import com.bstek.ureport.definition.dataset.Parameter;
import com.bstek.ureport.definition.value.AggregateType;
import com.bstek.ureport.definition.value.Source;
import com.bstek.ureport.definition.value.ValueType;
import com.bstek.ureport.model.Column;
import com.bstek.ureport.model.Row;

import java.util.ArrayList;
import java.util.List;

public class DefinitionEnumsAndPojosTest {

    @Test
    public void testAlignmentValues() {
        assertEquals("left", Alignment.left.name());
        assertEquals("right", Alignment.right.name());
        assertEquals("center", Alignment.center.name());
        assertEquals("top", Alignment.top.name());
        assertEquals("middle", Alignment.middle.name());
        assertEquals("bottom", Alignment.bottom.name());
    }

    @Test
    public void testBandValues() {
        assertEquals("headerrepeat", Band.headerrepeat.name());
        assertEquals("footerrepeat", Band.footerrepeat.name());
        assertEquals("title", Band.title.name());
        assertEquals("summary", Band.summary.name());
    }

    @Test
    public void testBorderStyleToBorderStyle() {
        assertEquals(BorderStyle.solid, BorderStyle.toBorderStyle("solid"));
        assertEquals(BorderStyle.dashed, BorderStyle.toBorderStyle("dashed"));
        assertEquals(BorderStyle.doublesolid, BorderStyle.toBorderStyle("double"));
    }

    @Test
    public void testBorderStyleToString() {
        assertEquals("solid", BorderStyle.solid.toString());
        assertEquals("dashed", BorderStyle.dashed.toString());
        assertEquals("double", BorderStyle.doublesolid.toString());
    }

    @Test
    public void testExpandValues() {
        assertEquals("None", Expand.None.name());
        assertEquals("Down", Expand.Down.name());
        assertEquals("Right", Expand.Right.name());
    }

    @Test
    public void testHtmlReportAlignValues() {
        assertEquals("left", HtmlReportAlign.left.name());
        assertEquals("center", HtmlReportAlign.center.name());
        assertEquals("right", HtmlReportAlign.right.name());
    }

    @Test
    public void testOrderValues() {
        assertEquals("asc", Order.asc.name());
        assertEquals("desc", Order.desc.name());
    }

    @Test
    public void testOrientationValues() {
        assertEquals("portrait", Orientation.portrait.name());
        assertEquals("landscape", Orientation.landscape.name());
    }

    @Test
    public void testPagingModeValues() {
        assertEquals("fitpage", PagingMode.fitpage.name());
        assertEquals("fixrows", PagingMode.fixrows.name());
    }

    @Test
    public void testPagingPositionValues() {
        assertEquals("before", PagingPosition.before.name());
        assertEquals("after", PagingPosition.after.name());
    }

    @Test
    public void testScopeValues() {
        assertEquals("cell", Scope.cell.name());
        assertEquals("row", Scope.row.name());
        assertEquals("column", Scope.column.name());
    }

    @Test
    public void testDatasourceTypeValues() {
        assertEquals("jdbc", DatasourceType.jdbc.name());
        assertEquals("spring", DatasourceType.spring.name());
        assertEquals("buildin", DatasourceType.buildin.name());
    }

    @Test
    public void testValueTypeValues() {
        assertEquals("simple", ValueType.simple.name());
        assertEquals("expression", ValueType.expression.name());
        assertEquals("dataset", ValueType.dataset.name());
        assertEquals("image", ValueType.image.name());
        assertEquals("chart", ValueType.chart.name());
        assertEquals("slash", ValueType.slash.name());
        assertEquals("zxing", ValueType.zxing.name());
    }

    @Test
    public void testAggregateTypeValues() {
        assertEquals("group", AggregateType.group.name());
        assertEquals("sum", AggregateType.sum.name());
        assertEquals("avg", AggregateType.avg.name());
        assertEquals("max", AggregateType.max.name());
        assertEquals("min", AggregateType.min.name());
        assertEquals("count", AggregateType.count.name());
    }

    @Test
    public void testSourceValues() {
        assertEquals("text", Source.text.name());
        assertEquals("expression", Source.expression.name());
    }

    @Test
    public void testBlankCellInfo() {
        BlankCellInfo info = new BlankCellInfo();
        assertEquals(0, info.getOffset());
        assertEquals(0, info.getSpan());
        assertFalse(info.isParent());

        BlankCellInfo info2 = new BlankCellInfo(5, 3, true);
        assertEquals(5, info2.getOffset());
        assertEquals(3, info2.getSpan());
        assertTrue(info2.isParent());
    }

    @Test
    public void testConditionPaging() {
        ConditionPaging cp = new ConditionPaging();
        cp.setPosition(PagingPosition.after);
        cp.setLine(3);
        assertEquals(PagingPosition.after, cp.getPosition());
        assertEquals(3, cp.getLine());
    }

    @Test
    public void testLinkParameter() {
        LinkParameter lp = new LinkParameter();
        lp.setName("param1");
        lp.setValue("value1");
        assertEquals("param1", lp.getName());
        assertEquals("value1", lp.getValue());
        assertNull(lp.getValueExpression());
    }

    @Test
    public void testConditionCellStyle() {
        ConditionCellStyle ccs = new ConditionCellStyle();
        assertEquals(Scope.cell, ccs.getBgcolorScope());
        assertEquals(Scope.cell, ccs.getForecolorScope());
        assertEquals(Scope.cell, ccs.getFontSizeScope());
        assertEquals(Scope.cell, ccs.getFontFamilyScope());
        assertEquals(Scope.cell, ccs.getAlignScope());
        assertEquals(Scope.cell, ccs.getValignScope());
        assertEquals(Scope.cell, ccs.getBoldScope());
        assertEquals(Scope.cell, ccs.getItalicScope());
        assertEquals(Scope.cell, ccs.getUnderlineScope());

        ccs.setBgcolorScope(Scope.row);
        ccs.setFontSize(14);
        ccs.setFontFamily("Arial");
        assertEquals(Scope.row, ccs.getBgcolorScope());
        assertEquals(14, ccs.getFontSize());
        assertEquals("Arial", ccs.getFontFamily());
    }

    @Test
    public void testPaperTypeGetPaperSize() {
        PaperSize a4 = PaperType.A4.getPaperSize();
        assertEquals(595, a4.getWidth());
        assertEquals(842, a4.getHeight());

        PaperSize a3 = PaperType.A3.getPaperSize();
        assertEquals(842, a3.getWidth());
        assertEquals(1191, a3.getHeight());

        PaperSize b5 = PaperType.B5.getPaperSize();
        assertEquals(499, b5.getWidth());
        assertEquals(709, b5.getHeight());
    }

    @Test
    public void testPaperTypeCustomThrows() {
        assertThrows(com.bstek.ureport.exception.ReportComputeException.class,
            () -> PaperType.CUSTOM.getPaperSize());
    }

    @Test
    public void testField() {
        Field field = new Field("name");
        assertEquals("name", field.getName());
        field.setName("age");
        assertEquals("age", field.getName());
    }

    @Test
    public void testParameter() {
        Parameter param = new Parameter();
        param.setName("p1");
        param.setDefaultValue("10");
        assertEquals("p1", param.getName());
        assertEquals("10", param.getDefaultValue());
        assertNull(param.getType());
    }

    @Test
    public void testRowNewRow() {
        List<Row> rows = new ArrayList<>();
        Row row = new Row(rows);
        rows.add(row);
        row.setHeight(30);
        row.setBand(Band.headerrepeat);
        row.setRowKey("key1");

        Row newRow = row.newRow();
        assertEquals(30, newRow.getHeight());
        assertEquals(Band.headerrepeat, newRow.getBand());
        assertEquals("key1", newRow.getRowKey());
    }

    @Test
    public void testRowGettersSetters() {
        List<Row> rows = new ArrayList<>();
        Row row = new Row(rows);
        rows.add(row);

        row.setHeight(50);
        row.setRealHeight(60);
        row.setPageIndex(2);
        row.setForPaging(true);
        row.setPageBreak(true);
        row.setHide(true);
        row.setTempRowNumber(10);
        row.setRowKey("rk1");

        assertEquals(50, row.getHeight());
        assertEquals(60, row.getRealHeight());
        assertEquals(2, row.getPageIndex());
        assertTrue(row.isForPaging());
        assertTrue(row.isPageBreak());
        assertTrue(row.isHide());
        assertEquals(10, row.getTempRowNumber());
        assertEquals("rk1", row.getRowKey());
        assertEquals(1, row.getRowNumber());
    }

    @Test
    public void testRowRealHeightDefault() {
        List<Row> rows = new ArrayList<>();
        Row row = new Row(rows);
        rows.add(row);
        row.setHeight(40);
        assertEquals(40, row.getRealHeight());
    }

    @Test
    public void testColumnNewColumn() {
        List<Column> cols = new ArrayList<>();
        Column col = new Column(cols);
        cols.add(col);
        col.setWidth(100);

        Column newCol = col.newColumn();
        assertEquals(100, newCol.getWidth());
    }

    @Test
    public void testColumnGettersSetters() {
        List<Column> cols = new ArrayList<>();
        Column col = new Column(cols);
        cols.add(col);

        col.setWidth(80);
        col.setHide(true);
        col.setTempColumnNumber(5);

        assertEquals(80, col.getWidth());
        assertTrue(col.isHide());
        assertEquals(5, col.getTempColumnNumber());
        assertEquals(1, col.getColumnNumber());
    }

    @Test
    public void testLine() {
        List<Row> rows = new ArrayList<>();
        Row row = new Row(rows);
        rows.add(row);

        assertNull(row.getCustomCellStyle());
        assertNotNull(row.getCells());
        assertTrue(row.getCells().isEmpty());

        CellStyle style = new CellStyle();
        style.setFontSize(12);
        row.setCustomCellStyle(style);
        assertSame(style, row.getCustomCellStyle());
    }
}
