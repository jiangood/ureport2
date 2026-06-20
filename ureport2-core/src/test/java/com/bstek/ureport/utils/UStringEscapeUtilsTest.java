package com.bstek.ureport.utils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UStringEscapeUtilsTest {

    @Test
    public void testEscapeNull() {
        assertNull(UStringEscapeUtils.escapeHtml4(null));
    }

    @Test
    public void testEscapeEmpty() {
        assertEquals("", UStringEscapeUtils.escapeHtml4(""));
    }

    @Test
    public void testEscapePlainText() {
        assertEquals("hello", UStringEscapeUtils.escapeHtml4("hello"));
    }

    @Test
    public void testEscapeAmpersand() {
        assertEquals("&amp;", UStringEscapeUtils.escapeHtml4("&"));
    }

    @Test
    public void testEscapeLessThan() {
        assertEquals("&lt;", UStringEscapeUtils.escapeHtml4("<"));
    }

    @Test
    public void testEscapeGreaterThan() {
        assertEquals("&gt;", UStringEscapeUtils.escapeHtml4(">"));
    }

    @Test
    public void testEscapeDoubleQuote() {
        assertEquals("&quot;", UStringEscapeUtils.escapeHtml4("\""));
    }

    @Test
    public void testEscapeSingleQuote() {
        assertEquals("&#x27;", UStringEscapeUtils.escapeHtml4("'"));
    }

    @Test
    public void testEscapeSlash() {
        assertEquals("&#x2F;", UStringEscapeUtils.escapeHtml4("/"));
    }

    @Test
    public void testEscapeCombined() {
        assertEquals("&lt;a href=&quot;x&quot;&gt;", UStringEscapeUtils.escapeHtml4("<a href=\"x\">"));
    }
}
