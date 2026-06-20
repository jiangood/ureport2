package com.bstek.ureport.expression;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ErrorInfoTest {

    @Test
    public void testConstructor() {
        ErrorInfo info = new ErrorInfo(5, 10, "syntax error");
        assertEquals(5, info.getLine());
        assertEquals(10, info.getCharPositionInLine());
        assertEquals("syntax error", info.getMessage());
    }

    @Test
    public void testGettersSetters() {
        ErrorInfo info = new ErrorInfo(0, 0, "");
        info.setLine(3);
        info.setCharPositionInLine(15);
        info.setMessage("unexpected token");
        assertEquals(3, info.getLine());
        assertEquals(15, info.getCharPositionInLine());
        assertEquals("unexpected token", info.getMessage());
    }
}
