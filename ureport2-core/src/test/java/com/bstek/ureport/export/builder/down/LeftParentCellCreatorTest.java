package com.bstek.ureport.export.builder.down;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.Range;
import com.bstek.ureport.definition.CellDefinition;

import java.util.List;

public class LeftParentCellCreatorTest {

    @Test
    public void testBuildParentCellsNoParents() {
        CellDefinition cell = new CellDefinition();
        cell.setName("A1");
        cell.setRowNumber(1);
        cell.setColumnNumber(1);

        List<Range> ranges = new LeftParentCellCreator().buildParentCells(cell);
        assertNotNull(ranges);
        assertEquals(1, ranges.size());
        assertEquals(1, ranges.get(0).getStart());
        assertEquals(1, ranges.get(0).getEnd());
    }

    @Test
    public void testBuildParentCellsSameRowLeftParentGoesToIncreaseSpan() {
        CellDefinition leftParent = new CellDefinition();
        leftParent.setName("A1");
        leftParent.setRowNumber(1);
        leftParent.setColumnNumber(1);

        CellDefinition cell = new CellDefinition();
        cell.setName("B1");
        cell.setRowNumber(1);
        cell.setColumnNumber(2);
        cell.setLeftParentCell(leftParent);

        List<Range> ranges = new LeftParentCellCreator().buildParentCells(cell);
        assertNotNull(ranges);
        assertEquals(1, ranges.size());
        assertTrue(cell.getIncreaseSpanCellNames().contains("A1"));
    }

    @Test
    public void testBuildParentCellsWithRowChildrenExtendsRange() {
        CellDefinition cell = new CellDefinition();
        cell.setName("B2");
        cell.setRowNumber(2);
        cell.setColumnNumber(1);

        CellDefinition child1 = new CellDefinition();
        child1.setName("C1");
        child1.setRowNumber(3);
        child1.setColumnNumber(1);
        child1.setRowSpan(2);
        cell.getRowChildrenCells().add(child1);

        List<Range> ranges = new LeftParentCellCreator().buildParentCells(cell);
        assertNotNull(ranges);
        assertEquals(1, ranges.size());
        assertEquals(2, ranges.get(0).getStart());
        assertEquals(2, ranges.get(0).getEnd());
    }

    @Test
    public void testBuildParentCellsChainOverlapCreatesBlankRanges() {
        CellDefinition gp = new CellDefinition();
        gp.setName("A1");
        gp.setRowNumber(1);
        gp.setColumnNumber(1);
        gp.setRowSpan(3);

        CellDefinition parent = new CellDefinition();
        parent.setName("A2");
        parent.setRowNumber(2);
        parent.setColumnNumber(1);
        parent.setLeftParentCell(gp);

        CellDefinition cell = new CellDefinition();
        cell.setName("A3");
        cell.setRowNumber(3);
        cell.setColumnNumber(1);
        cell.setLeftParentCell(parent);

        List<Range> ranges = new LeftParentCellCreator().buildParentCells(cell);
        assertNotNull(ranges);
        assertEquals(3, ranges.size());
        assertEquals(3, ranges.get(0).getStart());
        assertEquals(3, ranges.get(0).getEnd());
        assertTrue(cell.getNewBlankCellsMap().containsKey("A2"));
        assertTrue(cell.getNewBlankCellsMap().containsKey("A1"));
    }

    @Test
    public void testBuildParentCellsWithLeftParentAndChildren() {
        CellDefinition leftParent = new CellDefinition();
        leftParent.setName("A1");
        leftParent.setRowNumber(1);
        leftParent.setColumnNumber(1);

        CellDefinition cell = new CellDefinition();
        cell.setName("B2");
        cell.setRowNumber(2);
        cell.setColumnNumber(1);
        cell.setLeftParentCell(leftParent);

        CellDefinition child = new CellDefinition();
        child.setName("C1");
        child.setRowNumber(3);
        child.setColumnNumber(1);
        cell.getRowChildrenCells().add(child);

        List<Range> ranges = new LeftParentCellCreator().buildParentCells(cell);
        assertNotNull(ranges);
        assertEquals(1, ranges.size());
        assertEquals(2, ranges.get(0).getStart());
        assertEquals(2, ranges.get(0).getEnd());
    }
}
