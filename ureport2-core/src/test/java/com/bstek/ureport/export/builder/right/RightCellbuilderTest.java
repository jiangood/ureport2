package com.bstek.ureport.export.builder.right;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.definition.CellDefinition;

import java.util.ArrayList;
import java.util.List;

public class RightCellbuilderTest {

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
        CellDefinition b1 = cell("B1", 1, 2);
        b1.setTopParentCell(a1);

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(a1);
        cells.add(b1);

        RightCellbuilder builder = new RightCellbuilder();
        builder.buildParentCell(b1, cells);

        assertNotNull(b1.getDuplicateRange());
        assertTrue(b1.getNewCellNames().isEmpty());
    }

    @Test
    public void testBuildParentCellWithColumnChildren() {
        CellDefinition a1 = cell("A1", 1, 1);
        CellDefinition b1 = cell("B1", 1, 2);
        b1.setTopParentCell(a1);

        CellDefinition child = cell("C1", 1, 3);
        b1.getColumnChildrenCells().add(child);

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(a1);
        cells.add(b1);
        cells.add(child);

        RightCellbuilder builder = new RightCellbuilder();
        builder.buildParentCell(b1, cells);

        assertNotNull(b1.getDuplicateRange());
        assertTrue(b1.getNewCellNames().contains("C1"));
    }

    @Test
    public void testBuildParentCellNoTopParent() {
        CellDefinition a1 = cell("A1", 1, 1);

        List<CellDefinition> cells = new ArrayList<>();
        cells.add(a1);

        RightCellbuilder builder = new RightCellbuilder();
        builder.buildParentCell(a1, cells);

        assertNotNull(a1.getDuplicateRange());
    }
}
