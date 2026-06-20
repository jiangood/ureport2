package com.bstek.ureport.parser;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.definition.CellDefinition;

public class BuildUtilsTest {

    @Test
    public void testBuildRowNumberEndNoSpan() {
        CellDefinition cell = new CellDefinition();
        cell.setRowSpan(0);
        assertEquals(1, BuildUtils.buildRowNumberEnd(cell, 1));
        assertEquals(5, BuildUtils.buildRowNumberEnd(cell, 5));
    }

    @Test
    public void testBuildRowNumberEndWithSpan() {
        CellDefinition cell = new CellDefinition();
        cell.setRowSpan(3);
        assertEquals(4, BuildUtils.buildRowNumberEnd(cell, 2));
        assertEquals(5, BuildUtils.buildRowNumberEnd(cell, 3));
    }

    @Test
    public void testBuildRowNumberEndWithSpanOne() {
        CellDefinition cell = new CellDefinition();
        cell.setRowSpan(1);
        assertEquals(2, BuildUtils.buildRowNumberEnd(cell, 2));
    }

    @Test
    public void testBuildColNumberEndNoSpan() {
        CellDefinition cell = new CellDefinition();
        cell.setColSpan(0);
        assertEquals(1, BuildUtils.buildColNumberEnd(cell, 1));
        assertEquals(7, BuildUtils.buildColNumberEnd(cell, 7));
    }

    @Test
    public void testBuildColNumberEndWithSpan() {
        CellDefinition cell = new CellDefinition();
        cell.setColSpan(4);
        assertEquals(5, BuildUtils.buildColNumberEnd(cell, 2));
        assertEquals(10, BuildUtils.buildColNumberEnd(cell, 7));
    }

    @Test
    public void testBuildColNumberEndWithSpanOne() {
        CellDefinition cell = new CellDefinition();
        cell.setColSpan(1);
        assertEquals(3, BuildUtils.buildColNumberEnd(cell, 3));
    }
}
