package com.bstek.ureport.expression.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.bstek.ureport.exception.ReportParseException;
import com.bstek.ureport.expression.model.condition.ConditionType;
import com.bstek.ureport.expression.model.condition.Join;
import com.bstek.ureport.expression.model.expr.set.CellCoordinate;
import com.bstek.ureport.expression.model.expr.set.CellCoordinateSet;
import com.bstek.ureport.expression.model.expr.set.CoordinateType;

import java.util.Arrays;
import java.util.List;

public class ExpressionModelEnumsTest {

    /* ===== Operator ===== */
    @Test
    public void testOperatorParse() {
        assertEquals(Operator.Add, Operator.parse("+"));
        assertEquals(Operator.Subtract, Operator.parse("-"));
        assertEquals(Operator.Multiply, Operator.parse("*"));
        assertEquals(Operator.Divide, Operator.parse("/"));
        assertEquals(Operator.Complementation, Operator.parse("%"));
    }

    @Test
    public void testOperatorParseInvalid() {
        assertThrows(ReportParseException.class, () -> Operator.parse("^"));
        assertThrows(ReportParseException.class, () -> Operator.parse(""));
        assertThrows(ReportParseException.class, () -> Operator.parse("plus"));
    }

    @Test
    public void testOperatorToString() {
        assertEquals("+", Operator.Add.toString());
        assertEquals("-", Operator.Subtract.toString());
        assertEquals("*", Operator.Multiply.toString());
        assertEquals("/", Operator.Divide.toString());
        assertEquals("%", Operator.Complementation.toString());
    }

    @Test
    public void testOperatorRoundTrip() {
        for (Operator op : Operator.values()) {
            assertEquals(op, Operator.parse(op.toString()));
        }
    }

    /* ===== Op ===== */
    @Test
    public void testOpParse() {
        assertEquals(Op.GreatThen, Op.parse(">"));
        assertEquals(Op.EqualsGreatThen, Op.parse(">="));
        assertEquals(Op.LessThen, Op.parse("<"));
        assertEquals(Op.EqualsLessThen, Op.parse("<="));
        assertEquals(Op.Equals, Op.parse("=="));
        assertEquals(Op.NotEquals, Op.parse("!="));
        assertEquals(Op.In, Op.parse("in"));
        assertEquals(Op.NotIn, Op.parse("not in"));
        assertEquals(Op.NotIn, Op.parse("not  in"));
        assertEquals(Op.Like, Op.parse("like"));
    }

    @Test
    public void testOpParseWithTrim() {
        assertEquals(Op.GreatThen, Op.parse(" > "));
        assertEquals(Op.Equals, Op.parse(" == "));
    }

    @Test
    public void testOpParseInvalid() {
        assertThrows(ReportParseException.class, () -> Op.parse(">>>"));
        assertThrows(ReportParseException.class, () -> Op.parse("=s"));
    }

    @Test
    public void testOpToString() {
        assertEquals(">", Op.GreatThen.toString());
        assertEquals(">=", Op.EqualsGreatThen.toString());
        assertEquals("<", Op.LessThen.toString());
        assertEquals("<=", Op.EqualsLessThen.toString());
        assertEquals("==", Op.Equals.toString());
        assertEquals("!=", Op.NotEquals.toString());
        assertEquals(" in ", Op.In.toString());
        assertEquals(" not in ", Op.NotIn.toString());
        assertEquals(" like ", Op.Like.toString());
    }

    @Test
    public void testOpRoundTrip() {
        for (Op op : Op.values()) {
            assertEquals(op, Op.parse(op.toString().trim()));
        }
    }

    /* ===== Join ===== */
    @Test
    public void testJoinParse() {
        assertEquals(Join.and, Join.parse("and"));
        assertEquals(Join.and, Join.parse("&&"));
        assertEquals(Join.or, Join.parse("or"));
        assertEquals(Join.or, Join.parse("||"));
    }

    @Test
    public void testJoinParseInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Join.parse("xor"));
        assertThrows(IllegalArgumentException.class, () -> Join.parse(""));
    }

    /* ===== UnitType ===== */
    @Test
    public void testUnitTypeValues() {
        assertEquals(2, UnitType.values().length);
        assertEquals(UnitType.Single, UnitType.valueOf("Single"));
        assertEquals(UnitType.Location, UnitType.valueOf("Location"));
    }

    /* ===== CoordinateType ===== */
    @Test
    public void testCoordinateTypeValues() {
        assertEquals(2, CoordinateType.values().length);
        assertEquals(CoordinateType.relative, CoordinateType.valueOf("relative"));
        assertEquals(CoordinateType.absolute, CoordinateType.valueOf("absolute"));
    }

    /* ===== ConditionType ===== */
    @Test
    public void testConditionTypeValues() {
        assertEquals(4, ConditionType.values().length);
        assertEquals(ConditionType.property, ConditionType.valueOf("property"));
        assertEquals(ConditionType.expression, ConditionType.valueOf("expression"));
        assertEquals(ConditionType.cell, ConditionType.valueOf("cell"));
        assertEquals(ConditionType.current, ConditionType.valueOf("current"));
    }

    /* ===== CellCoordinate ===== */
    @Test
    public void testCellCoordinate() {
        CellCoordinate coord = new CellCoordinate("A1", CoordinateType.relative);
        assertEquals("A1", coord.getCellName());
        assertEquals(CoordinateType.relative, coord.getCoordinateType());
        assertEquals(0, coord.getPosition());
        assertFalse(coord.isReverse());
        assertEquals("A1:0", coord.toString());

        coord.setPosition(3);
        coord.setReverse(true);
        coord.setCellName("B2");
        assertEquals("B2", coord.getCellName());
        assertEquals(3, coord.getPosition());
        assertTrue(coord.isReverse());
        assertEquals("B2:3", coord.toString());
    }

    /* ===== CellCoordinateSet ===== */
    @Test
    public void testCellCoordinateSet() {
        CellCoordinate c1 = new CellCoordinate("A1", CoordinateType.relative);
        CellCoordinate c2 = new CellCoordinate("B1", CoordinateType.absolute);
        List<CellCoordinate> coords = Arrays.asList(c1, c2);
        CellCoordinateSet set = new CellCoordinateSet(coords);
        assertEquals(coords, set.getCellCoordinates());
        assertEquals(2, set.getCellCoordinates().size());
    }
}
