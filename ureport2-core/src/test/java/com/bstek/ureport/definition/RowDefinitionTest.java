package com.bstek.ureport.definition;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.model.Row;

import java.util.ArrayList;
import java.util.List;

public class RowDefinitionTest {

    @Test
    public void testDefaultValues() {
        RowDefinition rd = new RowDefinition();
        assertEquals(0, rd.getRowNumber());
        assertEquals(0, rd.getHeight());
        assertNull(rd.getBand());
    }

    @Test
    public void testGettersSetters() {
        RowDefinition rd = new RowDefinition();
        rd.setRowNumber(5);
        rd.setHeight(30);
        rd.setBand(Band.headerrepeat);

        assertEquals(5, rd.getRowNumber());
        assertEquals(30, rd.getHeight());
        assertEquals(Band.headerrepeat, rd.getBand());
    }

    @Test
    public void testNewRowFactoryMethod() {
        RowDefinition rd = new RowDefinition();
        rd.setRowNumber(3);
        rd.setHeight(40);
        rd.setBand(Band.title);

        List<Row> rows = new ArrayList<>();
        Row row = rd.newRow(rows);

        assertEquals(40, row.getHeight());
        assertEquals(Band.title, row.getBand());
        assertEquals("r3", row.getRowKey());
    }

    @Test
    public void testCompareTo() {
        RowDefinition rd1 = new RowDefinition();
        rd1.setRowNumber(2);

        RowDefinition rd2 = new RowDefinition();
        rd2.setRowNumber(5);

        RowDefinition rd3 = new RowDefinition();
        rd3.setRowNumber(2);

        assertTrue(rd1.compareTo(rd2) < 0);
        assertTrue(rd2.compareTo(rd1) > 0);
        assertEquals(0, rd1.compareTo(rd3));
    }

    @Test
    public void testSorting() {
        List<RowDefinition> list = new ArrayList<>();
        RowDefinition rd1 = new RowDefinition();
        rd1.setRowNumber(3);
        RowDefinition rd2 = new RowDefinition();
        rd2.setRowNumber(1);
        RowDefinition rd3 = new RowDefinition();
        rd3.setRowNumber(2);

        list.add(rd1);
        list.add(rd2);
        list.add(rd3);
        java.util.Collections.sort(list);

        assertEquals(1, list.get(0).getRowNumber());
        assertEquals(2, list.get(1).getRowNumber());
        assertEquals(3, list.get(2).getRowNumber());
    }

}
