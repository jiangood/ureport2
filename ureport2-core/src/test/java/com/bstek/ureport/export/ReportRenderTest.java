package com.bstek.ureport.export;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.definition.CellDefinition;
import com.bstek.ureport.definition.Expand;
import com.bstek.ureport.definition.ReportDefinition;

import java.util.ArrayList;
import java.util.List;

public class ReportRenderTest {

    @Test
    public void testRebuildReportDefinitionAddsRowChildren() {
        CellDefinition a1 = new CellDefinition();
        a1.setName("A1");
        a1.setRowNumber(1);
        a1.setColumnNumber(1);
        a1.setExpand(Expand.None);

        CellDefinition a2 = new CellDefinition();
        a2.setName("A2");
        a2.setRowNumber(2);
        a2.setColumnNumber(1);
        a2.setExpand(Expand.Down);
        a2.setLeftParentCell(a1);

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(a1);
        cells.add(a2);

        ReportDefinition report = new ReportDefinition();
        report.setCells(cells);

        ReportRender render = new ReportRender();
        render.rebuildReportDefinition(report);

        assertTrue(a1.getRowChildrenCells().contains(a2));
        assertNotNull(a2.getDuplicateRange());
    }

    @Test
    public void testRebuildReportDefinitionAddsColumnChildren() {
        CellDefinition a1 = new CellDefinition();
        a1.setName("A1");
        a1.setColumnNumber(1);
        a1.setRowNumber(1);
        a1.setExpand(Expand.None);

        CellDefinition b1 = new CellDefinition();
        b1.setName("B1");
        b1.setColumnNumber(2);
        b1.setRowNumber(1);
        b1.setExpand(Expand.Right);
        b1.setTopParentCell(a1);

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(a1);
        cells.add(b1);

        ReportDefinition report = new ReportDefinition();
        report.setCells(cells);

        ReportRender render = new ReportRender();
        render.rebuildReportDefinition(report);

        assertTrue(a1.getColumnChildrenCells().contains(b1));
        assertNotNull(b1.getDuplicateRange());
    }

    @Test
    public void testAddRowChildCellRecursive() {
        CellDefinition a1 = new CellDefinition();
        a1.setName("A1");
        a1.setRowNumber(1);
        a1.setColumnNumber(1);

        CellDefinition a2 = new CellDefinition();
        a2.setName("A2");
        a2.setRowNumber(2);
        a2.setColumnNumber(1);
        a2.setLeftParentCell(a1);

        CellDefinition a3 = new CellDefinition();
        a3.setName("A3");
        a3.setRowNumber(3);
        a3.setColumnNumber(1);
        a3.setLeftParentCell(a2);

        assertTrue(a1.getRowChildrenCells().isEmpty());
        assertTrue(a2.getRowChildrenCells().isEmpty());
        assertTrue(a3.getRowChildrenCells().isEmpty());

        ReportRender render = new ReportRender();
        render.rebuildReportDefinition(reportWithCells(a1, a2, a3));

        assertTrue(a1.getRowChildrenCells().contains(a2));
        assertTrue(a1.getRowChildrenCells().contains(a3));
        assertTrue(a2.getRowChildrenCells().contains(a3));
    }

    @Test
    public void testAddColumnChildCellRecursive() {
        CellDefinition a1 = new CellDefinition();
        a1.setName("A1");
        a1.setColumnNumber(1);
        a1.setRowNumber(1);

        CellDefinition b1 = new CellDefinition();
        b1.setName("B1");
        b1.setColumnNumber(2);
        b1.setRowNumber(1);
        b1.setTopParentCell(a1);

        CellDefinition c1 = new CellDefinition();
        c1.setName("C1");
        c1.setColumnNumber(3);
        c1.setRowNumber(1);
        c1.setTopParentCell(b1);

        assertTrue(a1.getColumnChildrenCells().isEmpty());
        assertTrue(b1.getColumnChildrenCells().isEmpty());
        assertTrue(c1.getColumnChildrenCells().isEmpty());

        ReportRender render = new ReportRender();
        render.rebuildReportDefinition(reportWithCells(a1, b1, c1));

        assertTrue(a1.getColumnChildrenCells().contains(b1));
        assertTrue(a1.getColumnChildrenCells().contains(c1));
        assertTrue(b1.getColumnChildrenCells().contains(c1));
    }

    @Test
    public void testRebuildDefinitionNoExpandCells() {
        CellDefinition a1 = new CellDefinition();
        a1.setName("A1");
        a1.setRowNumber(1);
        a1.setColumnNumber(1);
        a1.setExpand(Expand.None);

        CellDefinition b1 = new CellDefinition();
        b1.setName("B1");
        b1.setRowNumber(1);
        b1.setColumnNumber(2);
        b1.setExpand(Expand.None);

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(a1);
        cells.add(b1);

        ReportDefinition report = new ReportDefinition();
        report.setCells(cells);

        ReportRender render = new ReportRender();
        render.rebuildReportDefinition(report);

        assertTrue(a1.getRowChildrenCells().isEmpty());
        assertTrue(b1.getRowChildrenCells().isEmpty());
        assertNull(a1.getDuplicateRange());
        assertNull(b1.getDuplicateRange());
    }

    private static ReportDefinition reportWithCells(CellDefinition... cellArray) {
        List<CellDefinition> cells = new ArrayList<>();
        for (CellDefinition c : cellArray) {
            cells.add(c);
        }
        ReportDefinition report = new ReportDefinition();
        report.setCells(cells);
        return report;
    }
}
