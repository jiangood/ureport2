package com.bstek.ureport.expression.model.expr.set;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.expression.model.Condition;
import com.bstek.ureport.model.Cell;

public class SetExpressionPojosTest {

    @Test
    public void testCellCoordinate() {
        CellCoordinate coord = new CellCoordinate("A1", CoordinateType.relative);
        coord.setPosition(1);
        coord.setReverse(true);

        assertEquals("A1", coord.getCellName());
        assertEquals(1, coord.getPosition());
        assertEquals(CoordinateType.relative, coord.getCoordinateType());
        assertTrue(coord.isReverse());
    }

    @Test
    public void testCellCoordinateDefaults() {
        CellCoordinate coord = new CellCoordinate("B1", CoordinateType.absolute);
        assertEquals(0, coord.getPosition());
        assertEquals(CoordinateType.absolute, coord.getCoordinateType());
        assertFalse(coord.isReverse());
    }

    @Test
    public void testCellCoordinateSet() {
        List<CellCoordinate> list = new ArrayList<>();
        list.add(new CellCoordinate("A1", CoordinateType.relative));
        list.add(new CellCoordinate("B1", CoordinateType.relative));

        CellCoordinateSet set = new CellCoordinateSet(list);
        assertEquals(2, set.getCellCoordinates().size());
    }

    @Test
    public void testCellExpression() {
        CellExpression expr = new CellExpression("A1");
        assertEquals("A1", expr.cellName);
    }

    @Test
    public void testCellExpressionSupportsPaging() {
        CellExpression expr = new CellExpression("A1");
        assertTrue(expr.supportPaging());
    }

    @Test
    public void testCellConditionExpressionConstructor() {
        Condition condition = new Condition() {
            @Override
            public boolean filter(Cell cell, Cell currentCell, Object obj, Context context) {
                return true;
            }
        };
        CellConditionExpression expr = new CellConditionExpression("A1", condition);
        assertEquals("A1", expr.cellName);
    }

    @Test
    public void testCellCoordinateExpressionTwoArg() {
        List<CellCoordinate> list = new ArrayList<>();
        list.add(new CellCoordinate("A1", CoordinateType.relative));
        CellCoordinateSet left = new CellCoordinateSet(list);

        CellCoordinateExpression expr = new CellCoordinateExpression("B1", left);
        assertEquals("B1", expr.cellName);
    }

    @Test
    public void testCellCoordinateExpressionThreeArgWithCondition() {
        List<CellCoordinate> list = new ArrayList<>();
        list.add(new CellCoordinate("A1", CoordinateType.relative));
        CellCoordinateSet left = new CellCoordinateSet(list);

        Condition condition = new Condition() {
            @Override
            public boolean filter(Cell cell, Cell currentCell, Object obj, Context context) {
                return true;
            }
        };

        CellCoordinateExpression expr = new CellCoordinateExpression("B1", left, condition);
        assertEquals("B1", expr.cellName);
    }

    @Test
    public void testCellCoordinateExpressionThreeArgWithTop() {
        List<CellCoordinate> list = new ArrayList<>();
        list.add(new CellCoordinate("A1", CoordinateType.relative));
        CellCoordinateSet left = new CellCoordinateSet(list);
        CellCoordinateSet top = new CellCoordinateSet(list);

        CellCoordinateExpression expr = new CellCoordinateExpression("B1", left, top);
        assertEquals("B1", expr.cellName);
    }

    @Test
    public void testCellCoordinateExpressionFourArg() {
        List<CellCoordinate> list = new ArrayList<>();
        list.add(new CellCoordinate("A1", CoordinateType.relative));
        CellCoordinateSet left = new CellCoordinateSet(list);
        CellCoordinateSet top = new CellCoordinateSet(list);

        Condition condition = new Condition() {
            @Override
            public boolean filter(Cell cell, Cell currentCell, Object obj, Context context) {
                return true;
            }
        };

        CellCoordinateExpression expr = new CellCoordinateExpression("B1", left, top, condition);
        assertEquals("B1", expr.cellName);
    }

    @Test
    public void testCoordinateType() {
        assertEquals("relative", CoordinateType.relative.name());
        assertEquals("absolute", CoordinateType.absolute.name());
    }
}
