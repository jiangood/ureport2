package com.bstek.ureport.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.definition.Expand;

public class ExceptionTest {

    @Test
    public void testReportException() {
        ReportException ex1 = new ReportException("test msg");
        assertEquals("test msg", ex1.getMessage());

        ReportException ex2 = new ReportException(new RuntimeException("cause"));
        assertTrue(ex2.getMessage().contains("cause"));
    }

    @Test
    public void testCellDependencyException() {
        CellDependencyException ex = new CellDependencyException();
        assertTrue(ex.getMessage().contains("cyclic dependency"));
    }

    @Test
    public void testCellNotExistException() {
        CellNotExistException ex = new CellNotExistException("A1");
        assertTrue(ex.getMessage().contains("A1"));
    }

    @Test
    public void testCellComputeException() {
        CellComputeException ex1 = new CellComputeException("msg");
        assertEquals("msg", ex1.getMessage());

        CellComputeException ex2 = new CellComputeException(new RuntimeException("cause"));
        assertTrue(ex2.getMessage().contains("cause"));
    }

    @Test
    public void testReportComputeException() {
        ReportComputeException ex1 = new ReportComputeException("msg");
        assertEquals("msg", ex1.getMessage());

        ReportComputeException ex2 = new ReportComputeException(new RuntimeException("cause"));
        assertTrue(ex2.getMessage().contains("cause"));
    }

    @Test
    public void testReportParseException() {
        ReportParseException ex1 = new ReportParseException("msg");
        assertEquals("msg", ex1.getMessage());

        ReportParseException ex2 = new ReportParseException(new RuntimeException("cause"));
        assertTrue(ex2.getMessage().contains("cause"));
    }

    @Test
    public void testReportPagingException() {
        ReportPagingException ex = new ReportPagingException("paging error");
        assertEquals("paging error", ex.getMessage());
    }

    @Test
    public void testIndependenceException() {
        IndependenceException ex = new IndependenceException("ind error");
        assertEquals("ind error", ex.getMessage());
    }

    @Test
    public void testExpressionParserException() {
        ExpressionParserException ex = new ExpressionParserException("parse error");
        assertEquals("parse error", ex.getMessage());
    }

    @Test
    public void testDatasetUndefinitionException() {
        DatasetUndefinitionException ex = new DatasetUndefinitionException("ds1");
        assertTrue(ex.getMessage().contains("ds1"));
    }

    @Test
    public void testConvertException() {
        ConvertException ex1 = new ConvertException("convert error");
        assertEquals("convert error", ex1.getMessage());

        ConvertException ex2 = new ConvertException(new RuntimeException("cause"));
        assertTrue(ex2.getMessage().contains("cause"));
    }
}
