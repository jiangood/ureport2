package com.bstek.ureport.definition;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.model.Column;

import java.util.ArrayList;
import java.util.List;

public class ColumnDefinitionTest {

    @Test
    public void testDefaultValues() {
        ColumnDefinition cd = new ColumnDefinition();
        assertEquals(0, cd.getColumnNumber());
        assertEquals(0, cd.getWidth());
        assertFalse(cd.isHide());
    }

    @Test
    public void testGettersSetters() {
        ColumnDefinition cd = new ColumnDefinition();
        cd.setColumnNumber(8);
        cd.setWidth(120);
        cd.setHide(true);

        assertEquals(8, cd.getColumnNumber());
        assertEquals(120, cd.getWidth());
        assertTrue(cd.isHide());
    }

    @Test
    public void testNewColumnFactoryMethod() {
        ColumnDefinition cd = new ColumnDefinition();
        cd.setColumnNumber(4);
        cd.setWidth(150);

        List<Column> columns = new ArrayList<>();
        Column col = cd.newColumn(columns);

        assertEquals(150, col.getWidth());
    }

    @Test
    public void testCompareTo() {
        ColumnDefinition cd1 = new ColumnDefinition();
        cd1.setColumnNumber(3);

        ColumnDefinition cd2 = new ColumnDefinition();
        cd2.setColumnNumber(7);

        ColumnDefinition cd3 = new ColumnDefinition();
        cd3.setColumnNumber(3);

        assertTrue(cd1.compareTo(cd2) < 0);
        assertTrue(cd2.compareTo(cd1) > 0);
        assertEquals(0, cd1.compareTo(cd3));
    }

    @Test
    public void testSorting() {
        List<ColumnDefinition> list = new ArrayList<>();
        ColumnDefinition cd1 = new ColumnDefinition();
        cd1.setColumnNumber(5);
        ColumnDefinition cd2 = new ColumnDefinition();
        cd2.setColumnNumber(1);
        ColumnDefinition cd3 = new ColumnDefinition();
        cd3.setColumnNumber(3);

        list.add(cd1);
        list.add(cd2);
        list.add(cd3);
        java.util.Collections.sort(list);

        assertEquals(1, list.get(0).getColumnNumber());
        assertEquals(3, list.get(1).getColumnNumber());
        assertEquals(5, list.get(2).getColumnNumber());
    }
}
