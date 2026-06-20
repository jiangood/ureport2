package com.bstek.ureport.parser;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.definition.CellDefinition;
import com.bstek.ureport.definition.ColumnDefinition;
import com.bstek.ureport.definition.Expand;
import com.bstek.ureport.definition.Orientation;
import com.bstek.ureport.definition.PaperType;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.definition.RowDefinition;
import com.bstek.ureport.definition.value.SimpleValue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ReportParserTest {

    private ReportParser parser = new ReportParser();

    private InputStream toInputStream(String xml) {
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testParseMinimal() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<ureport>" +
                "  <cell name=\"A1\" row=\"1\" col=\"1\"><simple-value>hello</simple-value></cell>" +
                "  <row row-number=\"1\" height=\"30\"/>" +
                "  <column col-number=\"1\" width=\"80\"/>" +
                "  <paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\"></paper>" +
                "</ureport>";

        ReportDefinition def = parser.parse(toInputStream(xml), "test.ureport.xml");
        assertNotNull(def);
        assertEquals("test.ureport.xml", def.getReportFullName());

        assertNotNull(def.getRows());
        assertEquals(1, def.getRows().size());
        RowDefinition row = def.getRows().get(0);
        assertEquals(1, row.getRowNumber());
        assertEquals(30, row.getHeight());

        assertNotNull(def.getColumns());
        assertEquals(1, def.getColumns().size());
        ColumnDefinition col = def.getColumns().get(0);
        assertEquals(1, col.getColumnNumber());
        assertEquals(80, col.getWidth());

        assertNotNull(def.getCells());
        assertEquals(1, def.getCells().size());
        CellDefinition cell = def.getCells().get(0);
        assertEquals("A1", cell.getName());
        assertEquals(1, cell.getRowNumber());
        assertEquals(1, cell.getColumnNumber());
        assertEquals(Expand.None, cell.getExpand());
        assertTrue(cell.getValue() instanceof SimpleValue);
        assertEquals("hello", ((SimpleValue) cell.getValue()).getValue());

        assertNotNull(def.getPaper());
        assertEquals(PaperType.A4, def.getPaper().getPaperType());
        assertEquals(Orientation.portrait, def.getPaper().getOrientation());
    }

    @Test
    public void testParseWithMultipleRowsAndColumns() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<ureport>" +
                "  <cell name=\"A1\" row=\"1\" col=\"1\"><simple-value>v1</simple-value></cell>" +
                "  <cell name=\"B1\" row=\"1\" col=\"2\"><simple-value>v2</simple-value></cell>" +
                "  <cell name=\"A2\" row=\"2\" col=\"1\"><simple-value>v3</simple-value></cell>" +
                "  <row row-number=\"1\" height=\"25\"/>" +
                "  <row row-number=\"2\" height=\"35\"/>" +
                "  <column col-number=\"1\" width=\"50\"/>" +
                "  <column col-number=\"2\" width=\"100\"/>" +
                "  <paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\"></paper>" +
                "</ureport>";

        ReportDefinition def = parser.parse(toInputStream(xml), "test.xml");
        assertEquals(2, def.getRows().size());
        assertEquals(2, def.getColumns().size());
        assertEquals(3, def.getCells().size());
        assertEquals(25, def.getRows().get(0).getHeight());
        assertEquals(35, def.getRows().get(1).getHeight());
        assertEquals(50, def.getColumns().get(0).getWidth());
        assertEquals(100, def.getColumns().get(1).getWidth());
    }

    @Test
    public void testParseRowsAreSorted() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<ureport>" +
                "  <cell name=\"A1\" row=\"3\" col=\"1\"><simple-value>v</simple-value></cell>" +
                "  <row row-number=\"3\" height=\"30\"/>" +
                "  <row row-number=\"1\" height=\"20\"/>" +
                "  <row row-number=\"2\" height=\"10\"/>" +
                "  <column col-number=\"1\" width=\"80\"/>" +
                "  <paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\"></paper>" +
                "</ureport>";

        ReportDefinition def = parser.parse(toInputStream(xml), "test.xml");
        assertEquals(3, def.getRows().size());
        assertEquals(1, def.getRows().get(0).getRowNumber());
        assertEquals(20, def.getRows().get(0).getHeight());
        assertEquals(2, def.getRows().get(1).getRowNumber());
        assertEquals(10, def.getRows().get(1).getHeight());
        assertEquals(3, def.getRows().get(2).getRowNumber());
        assertEquals(30, def.getRows().get(2).getHeight());
    }

    @Test
    public void testParseWithExpandAndSpan() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<ureport>" +
                "  <cell expand=\"Down\" name=\"A1\" row=\"1\" col=\"1\" row-span=\"2\" col-span=\"3\"><simple-value>x</simple-value></cell>" +
                "  <row row-number=\"1\" height=\"30\"/>" +
                "  <column col-number=\"1\" width=\"80\"/>" +
                "  <paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\"></paper>" +
                "</ureport>";

        ReportDefinition def = parser.parse(toInputStream(xml), "test.xml");
        CellDefinition cell = def.getCells().get(0);
        assertEquals(Expand.Down, cell.getExpand());
        assertEquals(2, cell.getRowSpan());
        assertEquals(3, cell.getColSpan());
    }

    @Test
    public void testParseWithLeftAndTopCell() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<ureport>" +
                "  <cell name=\"A1\" row=\"1\" col=\"1\"><simple-value>a1</simple-value></cell>" +
                "  <cell name=\"B1\" row=\"1\" col=\"2\" left-cell=\"A1\"><simple-value>b1</simple-value></cell>" +
                "  <cell name=\"A2\" row=\"2\" col=\"1\" top-cell=\"A1\"><simple-value>a2</simple-value></cell>" +
                "  <cell name=\"B2\" row=\"2\" col=\"2\" left-cell=\"A2\" top-cell=\"B1\"><simple-value>b2</simple-value></cell>" +
                "  <row row-number=\"1\" height=\"30\"/>" +
                "  <row row-number=\"2\" height=\"30\"/>" +
                "  <column col-number=\"1\" width=\"80\"/>" +
                "  <column col-number=\"2\" width=\"80\"/>" +
                "  <paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\"></paper>" +
                "</ureport>";

        ReportDefinition def = parser.parse(toInputStream(xml), "test.xml");
        List<CellDefinition> cells = def.getCells();

        CellDefinition a1 = findCellByName(cells, "A1");
        CellDefinition b1 = findCellByName(cells, "B1");
        CellDefinition a2 = findCellByName(cells, "A2");
        CellDefinition b2 = findCellByName(cells, "B2");

        assertNull(a1.getLeftParentCell());
        assertNull(a1.getTopParentCell());

        assertNotNull(b1.getLeftParentCell());
        assertSame(a1, b1.getLeftParentCell());
        assertNull(b1.getTopParentCell());

        assertNull(a2.getLeftParentCell());
        assertNotNull(a2.getTopParentCell());
        assertSame(a1, a2.getTopParentCell());

        assertNotNull(b2.getLeftParentCell());
        assertSame(a2, b2.getLeftParentCell());
        assertNotNull(b2.getTopParentCell());
        assertSame(b1, b2.getTopParentCell());
    }

    @Test
    public void testParseWithImplicitParentCells() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<ureport>" +
                "  <cell name=\"A1\" row=\"1\" col=\"1\"><simple-value>a1</simple-value></cell>" +
                "  <cell name=\"B1\" row=\"1\" col=\"2\"><simple-value>b1</simple-value></cell>" +
                "  <cell name=\"A2\" row=\"2\" col=\"1\"><simple-value>a2</simple-value></cell>" +
                "  <cell name=\"B2\" row=\"2\" col=\"2\"><simple-value>b2</simple-value></cell>" +
                "  <row row-number=\"1\" height=\"30\"/>" +
                "  <row row-number=\"2\" height=\"30\"/>" +
                "  <column col-number=\"1\" width=\"80\"/>" +
                "  <column col-number=\"2\" width=\"80\"/>" +
                "  <paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\"></paper>" +
                "</ureport>";

        ReportDefinition def = parser.parse(toInputStream(xml), "test.xml");
        List<CellDefinition> cells = def.getCells();

        CellDefinition a1 = findCellByName(cells, "A1");
        CellDefinition b1 = findCellByName(cells, "B1");
        CellDefinition a2 = findCellByName(cells, "A2");
        CellDefinition b2 = findCellByName(cells, "B2");

        assertNull(a1.getLeftParentCell());
        assertNull(a1.getTopParentCell());

        assertSame(a1, b1.getLeftParentCell());
        assertNull(b1.getTopParentCell());

        assertNull(a2.getLeftParentCell());
        assertSame(a1, a2.getTopParentCell());

        assertSame(a2, b2.getLeftParentCell());
        assertSame(b1, b2.getTopParentCell());
    }

    @Test
    public void testParseWithRowSpanImplicitParent() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<ureport>" +
                "  <cell name=\"A1\" row=\"1\" col=\"1\" row-span=\"2\"><simple-value>spanned</simple-value></cell>" +
                "  <cell name=\"A2\" row=\"2\" col=\"1\"><simple-value>should have top parent A1</simple-value></cell>" +
                "  <row row-number=\"1\" height=\"30\"/>" +
                "  <row row-number=\"2\" height=\"30\"/>" +
                "  <column col-number=\"1\" width=\"80\"/>" +
                "  <paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\"></paper>" +
                "</ureport>";

        ReportDefinition def = parser.parse(toInputStream(xml), "test.xml");
        List<CellDefinition> cells = def.getCells();

        CellDefinition a1 = findCellByName(cells, "A1");
        CellDefinition a2 = findCellByName(cells, "A2");

        assertNull(a1.getTopParentCell());
        assertSame(a1, a2.getTopParentCell());
    }

    @Test
    public void testParseWithFillBlankRows() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<ureport>" +
                "  <cell name=\"A1\" row=\"1\" col=\"1\" fill-blank-rows=\"true\" multiple=\"3\"><simple-value>x</simple-value></cell>" +
                "  <row row-number=\"1\" height=\"30\"/>" +
                "  <column col-number=\"1\" width=\"80\"/>" +
                "  <paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\"></paper>" +
                "</ureport>";

        ReportDefinition def = parser.parse(toInputStream(xml), "test.xml");
        CellDefinition cell = def.getCells().get(0);
        assertTrue(cell.isFillBlankRows());
        assertEquals(3, cell.getMultiple());
    }

    @Test
    public void testParseWithLinkUrl() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<ureport>" +
                "  <cell name=\"A1\" row=\"1\" col=\"1\" link-url=\"http://example.com\" link-target-window=\"_blank\"><simple-value>link</simple-value></cell>" +
                "  <row row-number=\"1\" height=\"30\"/>" +
                "  <column col-number=\"1\" width=\"80\"/>" +
                "  <paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\"></paper>" +
                "</ureport>";

        ReportDefinition def = parser.parse(toInputStream(xml), "test.xml");
        CellDefinition cell = def.getCells().get(0);
        assertEquals("http://example.com", cell.getLinkUrl());
        assertEquals("_blank", cell.getLinkTargetWindow());
    }

    @Test
    public void testParseWithBand() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<ureport>" +
                "  <cell name=\"A1\" row=\"1\" col=\"1\"><simple-value>h</simple-value></cell>" +
                "  <row row-number=\"1\" height=\"30\" band=\"headerrepeat\"/>" +
                "  <column col-number=\"1\" width=\"80\"/>" +
                "  <paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\"></paper>" +
                "</ureport>";

        ReportDefinition def = parser.parse(toInputStream(xml), "test.xml");
        assertEquals(1, def.getRows().size());
        assertEquals("headerrepeat", def.getRows().get(0).getBand().name());
    }

    @Test
    public void testParseWithHiddenColumn() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<ureport>" +
                "  <cell name=\"A1\" row=\"1\" col=\"1\"><simple-value>x</simple-value></cell>" +
                "  <row row-number=\"1\" height=\"30\"/>" +
                "  <column col-number=\"1\" width=\"80\" hide=\"true\"/>" +
                "  <paper type=\"A4\" orientation=\"portrait\" paging-mode=\"fitpage\"></paper>" +
                "</ureport>";

        ReportDefinition def = parser.parse(toInputStream(xml), "test.xml");
        assertTrue(def.getColumns().get(0).isHide());
    }

    private CellDefinition findCellByName(List<CellDefinition> cells, String name) {
        return cells.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }
}
