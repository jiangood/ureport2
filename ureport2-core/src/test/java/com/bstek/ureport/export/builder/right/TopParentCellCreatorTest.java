package com.bstek.ureport.export.builder.right;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.Range;
import com.bstek.ureport.definition.CellDefinition;

import java.util.List;

public class TopParentCellCreatorTest {

    @Test
    public void testBuildParentCellsNoParents() {
        CellDefinition cell = new CellDefinition();
        cell.setName("A1");
        cell.setColumnNumber(1);
        cell.setRowNumber(1);

        List<Range> ranges = new TopParentCellCreator().buildParentCells(cell);
        assertNotNull(ranges);
        assertEquals(1, ranges.size());
        assertEquals(1, ranges.get(0).getStart());
        assertEquals(1, ranges.get(0).getEnd());
    }

    @Test
    public void testBuildParentCellsSameColumnTopParentGoesToIncreaseSpan() {
        CellDefinition topParent = new CellDefinition();
        topParent.setName("A1");
        topParent.setColumnNumber(1);
        topParent.setRowNumber(1);

        CellDefinition cell = new CellDefinition();
        cell.setName("A2");
        cell.setColumnNumber(1);
        cell.setRowNumber(2);
        cell.setTopParentCell(topParent);

        List<Range> ranges = new TopParentCellCreator().buildParentCells(cell);
        assertNotNull(ranges);
        assertEquals(1, ranges.size());
        assertTrue(cell.getIncreaseSpanCellNames().contains("A1"));
    }

    @Test
    public void testBuildParentCellsWithColumnChildren() {
        CellDefinition cell = new CellDefinition();
        cell.setName("A1");
        cell.setColumnNumber(1);
        cell.setRowNumber(1);

        CellDefinition child = new CellDefinition();
        child.setName("A2");
        child.setColumnNumber(2);
        child.setRowNumber(1);
        cell.getColumnChildrenCells().add(child);

        List<Range> ranges = new TopParentCellCreator().buildParentCells(cell);
        assertNotNull(ranges);
        assertEquals(1, ranges.size());
        assertEquals(1, ranges.get(0).getStart());
        assertEquals(1, ranges.get(0).getEnd());
    }

    @Test
    public void testBuildParentCellsSameColumnChainAllIncreaseSpan() {
        CellDefinition a1 = new CellDefinition();
        a1.setName("A1");
        a1.setColumnNumber(1);
        a1.setRowNumber(1);

        CellDefinition a2 = new CellDefinition();
        a2.setName("A2");
        a2.setColumnNumber(1);
        a2.setRowNumber(2);
        a2.setTopParentCell(a1);

        CellDefinition a3 = new CellDefinition();
        a3.setName("A3");
        a3.setColumnNumber(1);
        a3.setRowNumber(3);
        a3.setTopParentCell(a2);

        List<Range> ranges = new TopParentCellCreator().buildParentCells(a3);
        assertNotNull(ranges);
        assertEquals(1, ranges.size());
        assertTrue(a3.getIncreaseSpanCellNames().contains("A1"));
        assertTrue(a3.getIncreaseSpanCellNames().contains("A2"));
    }
}
