package com.bstek.ureport.utils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UStringUtilsTest {

    @Test
    public void testIsBlank() {
        assertTrue(UStringUtils.isBlank(null));
        assertTrue(UStringUtils.isBlank(""));
        assertTrue(UStringUtils.isBlank("   "));
        assertFalse(UStringUtils.isBlank("a"));
        assertFalse(UStringUtils.isBlank(" a "));
    }

    @Test
    public void testIsNotBlank() {
        assertFalse(UStringUtils.isNotBlank(null));
        assertFalse(UStringUtils.isNotBlank(""));
        assertFalse(UStringUtils.isNotBlank("   "));
        assertTrue(UStringUtils.isNotBlank("a"));
        assertTrue(UStringUtils.isNotBlank(" a "));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(UStringUtils.isEmpty(null));
        assertTrue(UStringUtils.isEmpty(""));
        assertFalse(UStringUtils.isEmpty("   "));
        assertFalse(UStringUtils.isEmpty("a"));
    }

    @Test
    public void testIsNotEmpty() {
        assertFalse(UStringUtils.isNotEmpty(null));
        assertFalse(UStringUtils.isNotEmpty(""));
        assertTrue(UStringUtils.isNotEmpty("   "));
        assertTrue(UStringUtils.isNotEmpty("a"));
    }
}
