package com.bstek.ureport.export.builder.down;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.definition.CellDefinition;

import java.util.ArrayList;
import java.util.List;

public class DownCellbuilderTest {

    private CellDefinition cell(String name, int row, int col) {
        CellDefinition c = new CellDefinition();
        c.setName(name);
        c.setRowNumber(row);
        c.setColumnNumber(col);
        return c;
    }

    @Test
    public void testBuildParentCellNoChildren() {
        CellDefinition a1 = cell("A1", 1, 1);
        CellDefinition a2 = cell("A2", 2, 1);
        a2.setLeftParentCell(a1);

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(a1);
        cells.add(a2);

        DownCellbuilder builder = new DownCellbuilder();
        builder.buildParentCell(a2, cells);

        assertNotNull(a2.getDuplicateRange());
        assertTrue(a2.getNewCellNames().isEmpty());
    }

    @Test
    public void testBuildParentCellWithRowChildren() {
        CellDefinition a1 = cell("A1", 1, 1);
        CellDefinition a2 = cell("A2", 2, 1);
        a2.setLeftParentCell(a1);

        CellDefinition child = cell("B1", 3, 1);
        a2.getRowChildrenCells().add(child);

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(a1);
        cells.add(a2);
        cells.add(child);

        DownCellbuilder builder = new DownCellbuilder();
        builder.buildParentCell(a2, cells);

        assertNotNull(a2.getDuplicateRange());
        assertTrue(a2.getNewCellNames().contains("B1"));
    }

    @Test
    public void testBuildParentCellWithBlankCellsInRange() {
        CellDefinition a1 = cell("A1", 1, 1);
        CellDefinition a2 = cell("A2", 2, 1);
        a2.setLeftParentCell(a1);
        a2.setRowSpan(2);

        CellDefinition b1 = cell("B1", 3, 1);
        b1.setRowSpan(1);

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(a1);
        cells.add(a2);
        cells.add(b1);

        DownCellbuilder builder = new DownCellbuilder();
        builder.buildParentCell(a2, cells);

        assertNotNull(a2.getDuplicateRange());
        assertTrue(a2.getNewBlankCellsMap().containsKey("B1") ||
                   a2.getNewCellNames().contains("B1") ||
                   a2.getIncreaseSpanCellNames().contains("B1"));
    }

    @Test
    public void testBuildParentCellNoLeftParent() {
        CellDefinition a1 = cell("A1", 1, 1);

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(a1);

        DownCellbuilder builder = new DownCellbuilder();
        builder.buildParentCell(a1, cells);

        assertNotNull(a1.getDuplicateRange());
    }
}
