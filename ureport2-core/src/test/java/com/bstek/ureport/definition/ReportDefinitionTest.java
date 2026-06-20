package com.bstek.ureport.definition;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.definition.datasource.DatasourceDefinition;
import com.bstek.ureport.definition.value.SimpleValue;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.model.Column;
import com.bstek.ureport.model.Report;
import com.bstek.ureport.model.Row;

import java.util.ArrayList;
import java.util.List;

public class ReportDefinitionTest {

    @Test
    public void testDefaultValues() {
        ReportDefinition rd = new ReportDefinition();
        assertNull(rd.getReportFullName());
        assertNull(rd.getPaper());
        assertNull(rd.getRootCell());
        assertNull(rd.getHeader());
        assertNull(rd.getFooter());
        assertNull(rd.getSearchForm());
        assertNull(rd.getCells());
        assertNull(rd.getRows());
        assertNull(rd.getColumns());
        assertNull(rd.getDatasources());
        assertNull(rd.getSearchFormXml());
    }

    @Test
    public void testGetStyleWithEmptyCells() {
        ReportDefinition rd = new ReportDefinition();
        rd.setCells(new ArrayList<>());
        rd.setRows(new ArrayList<>());
        rd.setColumns(new ArrayList<>());
        assertEquals("", rd.getStyle());
    }

    @Test
    public void testGettersSetters() {
        ReportDefinition rd = new ReportDefinition();
        rd.setReportFullName("test.ureport.xml");
        assertEquals("test.ureport.xml", rd.getReportFullName());

        Paper paper = new Paper();
        paper.setWidth(595);
        rd.setPaper(paper);
        assertSame(paper, rd.getPaper());

        CellDefinition rootCell = new CellDefinition();
        rootCell.setName("A1");
        rd.setRootCell(rootCell);
        assertSame(rootCell, rd.getRootCell());

        HeaderFooterDefinition header = new HeaderFooterDefinition();
        rd.setHeader(header);
        assertSame(header, rd.getHeader());

        HeaderFooterDefinition footer = new HeaderFooterDefinition();
        rd.setFooter(footer);
        assertSame(footer, rd.getFooter());

        List<RowDefinition> rows = new ArrayList<>();
        rd.setRows(rows);
        assertSame(rows, rd.getRows());

        List<ColumnDefinition> columns = new ArrayList<>();
        rd.setColumns(columns);
        assertSame(columns, rd.getColumns());

        List<CellDefinition> cells = new ArrayList<>();
        rd.setCells(cells);
        assertSame(cells, rd.getCells());

        List<DatasourceDefinition> datasources = new ArrayList<>();
        rd.setDatasources(datasources);
        assertSame(datasources, rd.getDatasources());

        rd.setSearchFormXml("<form/>");
        assertEquals("<form/>", rd.getSearchFormXml());
    }

    private ReportDefinition buildMinimalReportDefinition() {
        ReportDefinition rd = new ReportDefinition();
        rd.setReportFullName("test.ureport.xml");

        RowDefinition row1 = new RowDefinition();
        row1.setRowNumber(1);
        row1.setHeight(30);

        RowDefinition row2 = new RowDefinition();
        row2.setRowNumber(2);
        row2.setHeight(40);
        row2.setBand(Band.headerrepeat);

        RowDefinition row3 = new RowDefinition();
        row3.setRowNumber(3);
        row3.setHeight(50);
        row3.setBand(Band.title);

        RowDefinition row4 = new RowDefinition();
        row4.setRowNumber(4);
        row4.setHeight(25);
        row4.setBand(Band.footerrepeat);

        RowDefinition row5 = new RowDefinition();
        row5.setRowNumber(5);
        row5.setHeight(35);
        row5.setBand(Band.summary);

        List<RowDefinition> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);
        rd.setRows(rows);

        ColumnDefinition col1 = new ColumnDefinition();
        col1.setColumnNumber(1);
        col1.setWidth(100);

        ColumnDefinition col2 = new ColumnDefinition();
        col2.setColumnNumber(2);
        col2.setWidth(200);

        List<ColumnDefinition> columns = new ArrayList<>();
        columns.add(col1);
        columns.add(col2);
        rd.setColumns(columns);

        CellDefinition a1 = new CellDefinition();
        a1.setName("A1");
        a1.setRowNumber(1);
        a1.setColumnNumber(1);
        a1.setValue(new SimpleValue("title"));

        CellDefinition b1 = new CellDefinition();
        b1.setName("B1");
        b1.setRowNumber(1);
        b1.setColumnNumber(2);
        b1.setValue(new SimpleValue("value"));

        CellDefinition a2 = new CellDefinition();
        a2.setName("A2");
        a2.setRowNumber(2);
        a2.setColumnNumber(1);
        a2.setValue(new SimpleValue("data1"));

        CellDefinition b2 = new CellDefinition();
        b2.setName("B2");
        b2.setRowNumber(2);
        b2.setColumnNumber(2);
        b2.setValue(new SimpleValue("data2"));

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(a1);
        cells.add(b1);
        cells.add(a2);
        cells.add(b2);
        rd.setCells(cells);

        return rd;
    }

    @Test
    public void testNewReportBasic() {
        ReportDefinition rd = buildMinimalReportDefinition();
        Report report = rd.newReport();

        assertEquals("test.ureport.xml", report.getReportFullName());
        assertNotNull(report.getRows());
        assertEquals(5, report.getRows().size());
        assertNotNull(report.getColumns());
        assertEquals(2, report.getColumns().size());

        Row r1 = report.getRow(1);
        assertEquals(30, r1.getHeight());
        Row r2 = report.getRow(2);
        assertEquals(40, r2.getHeight());
        Row r3 = report.getRow(3);
        assertEquals(50, r3.getHeight());
        Row r4 = report.getRow(4);
        assertEquals(25, r4.getHeight());
        Row r5 = report.getRow(5);
        assertEquals(35, r5.getHeight());

        Column c1 = report.getColumn(1);
        assertEquals(100, c1.getWidth());
        Column c2 = report.getColumn(2);
        assertEquals(200, c2.getWidth());

        assertEquals(1, report.getHeaderRepeatRows().size());
        assertSame(r2, report.getHeaderRepeatRows().get(0));
        assertEquals(1, report.getFooterRepeatRows().size());
        assertSame(r4, report.getFooterRepeatRows().get(0));
        assertEquals(1, report.getTitleRows().size());
        assertSame(r3, report.getTitleRows().get(0));
        assertEquals(1, report.getSummaryRows().size());
        assertSame(r5, report.getSummaryRows().get(0));

        assertEquals(40, report.getRepeatHeaderRowHeight());
        assertEquals(25, report.getRepeatFooterRowHeight());
        assertEquals(50, report.getTitleRowsHeight());
        assertEquals(35, report.getSummaryRowsHeight());
    }

    @Test
    public void testNewReportCells() {
        ReportDefinition rd = buildMinimalReportDefinition();
        Report report = rd.newReport();

        assertEquals(4, report.getCellsMap().size());
        List<Cell> a1Cells = report.getCellsMap().get("A1");
        assertNotNull(a1Cells);
        assertEquals(1, a1Cells.size());
        Cell a1 = a1Cells.get(0);
        assertEquals("title", ((SimpleValue) a1.getValue()).getValue());
        assertSame(report.getRow(1), a1.getRow());
        assertSame(report.getColumn(1), a1.getColumn());

        Cell b1 = report.getCellsMap().get("B1").get(0);
        assertEquals("value", ((SimpleValue) b1.getValue()).getValue());
        assertSame(report.getRow(1), b1.getRow());
        assertSame(report.getColumn(2), b1.getColumn());

        Cell a2 = report.getCellsMap().get("A2").get(0);
        assertSame(report.getRow(2), a2.getRow());
        assertSame(report.getColumn(1), a2.getColumn());
    }

    @Test
    public void testNewReportWithParentCells() {
        ReportDefinition rd = new ReportDefinition();
        rd.setReportFullName("test.xml");

        RowDefinition row1 = new RowDefinition();
        row1.setRowNumber(1);
        row1.setHeight(30);
        RowDefinition row2 = new RowDefinition();
        row2.setRowNumber(2);
        row2.setHeight(30);
        List<RowDefinition> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        rd.setRows(rows);

        ColumnDefinition col1 = new ColumnDefinition();
        col1.setColumnNumber(1);
        col1.setWidth(100);
        ColumnDefinition col2 = new ColumnDefinition();
        col2.setColumnNumber(2);
        col2.setWidth(100);
        List<ColumnDefinition> columns = new ArrayList<>();
        columns.add(col1);
        columns.add(col2);
        rd.setColumns(columns);

        CellDefinition a1 = new CellDefinition();
        a1.setName("A1");
        a1.setRowNumber(1);
        a1.setColumnNumber(1);
        a1.setValue(new SimpleValue("v1"));

        CellDefinition b1 = new CellDefinition();
        b1.setName("B1");
        b1.setRowNumber(1);
        b1.setColumnNumber(2);
        b1.setValue(new SimpleValue("v2"));
        b1.setLeftParentCell(a1);
        a1.getRowChildrenCells().add(b1);

        CellDefinition a2 = new CellDefinition();
        a2.setName("A2");
        a2.setRowNumber(2);
        a2.setColumnNumber(1);
        a2.setValue(new SimpleValue("v3"));
        a2.setTopParentCell(a1);
        a1.getColumnChildrenCells().add(a2);

        CellDefinition b2 = new CellDefinition();
        b2.setName("B2");
        b2.setRowNumber(2);
        b2.setColumnNumber(2);
        b2.setValue(new SimpleValue("v4"));
        b2.setLeftParentCell(a2);
        b2.setTopParentCell(b1);
        a2.getRowChildrenCells().add(b2);
        b1.getColumnChildrenCells().add(b2);

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(a1);
        cells.add(b1);
        cells.add(a2);
        cells.add(b2);
        rd.setCells(cells);

        Report report = rd.newReport();

        Cell cellA1 = report.getCellsMap().get("A1").get(0);
        Cell cellB1 = report.getCellsMap().get("B1").get(0);
        Cell cellA2 = report.getCellsMap().get("A2").get(0);
        Cell cellB2 = report.getCellsMap().get("B2").get(0);

        assertNull(cellA1.getLeftParentCell());
        assertNull(cellA1.getTopParentCell());
        assertSame(cellA1, report.getRootCell());

        assertSame(cellA1, cellB1.getLeftParentCell());
        assertNull(cellB1.getTopParentCell());

        assertNull(cellA2.getLeftParentCell());
        assertSame(cellA1, cellA2.getTopParentCell());

        assertSame(cellA2, cellB2.getLeftParentCell());
        assertSame(cellB1, cellB2.getTopParentCell());
    }

    @Test
    public void testGetStyle() {
        ReportDefinition rd = new ReportDefinition();

        RowDefinition row1 = new RowDefinition();
        row1.setRowNumber(1);
        row1.setHeight(30);
        List<RowDefinition> rows = new ArrayList<>();
        rows.add(row1);
        rd.setRows(rows);

        ColumnDefinition col1 = new ColumnDefinition();
        col1.setColumnNumber(1);
        col1.setWidth(80);
        List<ColumnDefinition> columns = new ArrayList<>();
        columns.add(col1);
        rd.setColumns(columns);

        CellDefinition cell = new CellDefinition();
        cell.setName("A1");
        cell.setRowNumber(1);
        cell.setColumnNumber(1);
        cell.setValue(new SimpleValue("test"));

        CellStyle style = cell.getCellStyle();
        style.setFontSize(12);
        style.setFontFamily("Arial");
        style.setAlign(Alignment.center);
        style.setValign(Alignment.middle);
        style.setBold(true);
        style.setBgcolor("255,0,0");
        style.setForecolor("0,0,255");

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(cell);
        rd.setCells(cells);

        String css = rd.getStyle();
        assertNotNull(css);
        assertTrue(css.contains("._A1{"));
        assertTrue(css.contains("width:80pt;"));
        assertTrue(css.contains("text-align:center;"));
        assertTrue(css.contains("vertical-align:middle;"));
        assertTrue(css.contains("background-color:rgb(255,0,0);"));
        assertTrue(css.contains("color:rgb(0,0,255);"));
        assertTrue(css.contains("font-family:Arial;"));
        assertTrue(css.contains("font-size:12pt;"));
        assertTrue(css.contains("font-weight:bold;"));
    }

    @Test
    public void testGetStyleWithBorders() {
        ReportDefinition rd = new ReportDefinition();

        RowDefinition row1 = new RowDefinition();
        row1.setRowNumber(1);
        row1.setHeight(30);
        List<RowDefinition> rows = new ArrayList<>();
        rows.add(row1);
        rd.setRows(rows);

        ColumnDefinition col1 = new ColumnDefinition();
        col1.setColumnNumber(1);
        col1.setWidth(100);
        List<ColumnDefinition> columns = new ArrayList<>();
        columns.add(col1);
        rd.setColumns(columns);

        CellDefinition cell = new CellDefinition();
        cell.setName("B2");
        cell.setRowNumber(1);
        cell.setColumnNumber(1);
        cell.setValue(new SimpleValue("x"));

        CellStyle style = cell.getCellStyle();
        style.setFontSize(10);

        Border left = new Border();
        left.setWidth(1);
        left.setColor("0,0,0");
        left.setStyle(BorderStyle.solid);
        style.setLeftBorder(left);

        Border right = new Border();
        right.setWidth(2);
        right.setColor("255,0,0");
        right.setStyle(BorderStyle.dashed);
        style.setRightBorder(right);

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(cell);
        rd.setCells(cells);

        String css = rd.getStyle();
        assertTrue(css.contains("border-left:solid 1px rgb(0,0,0);"));
        assertTrue(css.contains("border-right:dashed 2px rgb(255,0,0);"));
    }

    @Test
    public void testGetStyleWithColSpan() {
        ReportDefinition rd = new ReportDefinition();

        RowDefinition row1 = new RowDefinition();
        row1.setRowNumber(1);
        row1.setHeight(30);
        List<RowDefinition> rows = new ArrayList<>();
        rows.add(row1);
        rd.setRows(rows);

        ColumnDefinition col1 = new ColumnDefinition();
        col1.setColumnNumber(1);
        col1.setWidth(50);
        ColumnDefinition col2 = new ColumnDefinition();
        col2.setColumnNumber(2);
        col2.setWidth(70);
        ColumnDefinition col3 = new ColumnDefinition();
        col3.setColumnNumber(3);
        col3.setWidth(80);
        List<ColumnDefinition> columns = new ArrayList<>();
        columns.add(col1);
        columns.add(col2);
        columns.add(col3);
        rd.setColumns(columns);

        CellDefinition cell = new CellDefinition();
        cell.setName("C1");
        cell.setRowNumber(1);
        cell.setColumnNumber(1);
        cell.setColSpan(2);
        cell.setValue(new SimpleValue("spanned"));

        CellStyle style = cell.getCellStyle();
        style.setFontSize(10);

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(cell);
        rd.setCells(cells);

        String css = rd.getStyle();
        int expectedWidth = 50 + 70;
        assertTrue(css.contains("width:" + expectedWidth + "pt;"), "Expected width " + expectedWidth + "pt for colSpan=2");
    }

    @Test
    public void testGetStyleIsCached() {
        ReportDefinition rd = new ReportDefinition();

        RowDefinition row1 = new RowDefinition();
        row1.setRowNumber(1);
        row1.setHeight(30);
        List<RowDefinition> rows = new ArrayList<>();
        rows.add(row1);
        rd.setRows(rows);

        ColumnDefinition col1 = new ColumnDefinition();
        col1.setColumnNumber(1);
        col1.setWidth(80);
        List<ColumnDefinition> columns = new ArrayList<>();
        columns.add(col1);
        rd.setColumns(columns);

        CellDefinition cell = new CellDefinition();
        cell.setName("A1");
        cell.setRowNumber(1);
        cell.setColumnNumber(1);
        cell.setValue(new SimpleValue("x"));
        cell.getCellStyle().setFontSize(10);
        List<CellDefinition> cells = new ArrayList<>();
        cells.add(cell);
        rd.setCells(cells);

        String css1 = rd.getStyle();
        String css2 = rd.getStyle();
        assertSame(css1, css2);
    }
}
