package com.bstek.ureport.export.word;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class DxaUtilsTest {

    @Test
    public void testDxa2points() {
        assertEquals(36.0f, DxaUtils.dxa2points(720.0f), 0.001f);
        assertEquals(0.0f, DxaUtils.dxa2points(0.0f), 0.001f);
    }

    @Test
    public void testDxa2pointsInt() {
        assertEquals(36, DxaUtils.dxa2points(720));
        assertEquals(0, DxaUtils.dxa2points(0));
    }

    @Test
    public void testDxa2pointsBigInteger() {
        assertEquals(36.0f, DxaUtils.dxa2points(BigInteger.valueOf(720)), 0.001f);
    }

    @Test
    public void testPoints2dxa() {
        assertEquals(420, DxaUtils.points2dxa(20));
        assertEquals(0, DxaUtils.points2dxa(0));
    }

    @Test
    public void testDxa2inch() {
        assertEquals(0.5f, DxaUtils.dxa2inch(720.0f), 0.001f);
    }

    @Test
    public void testDxa2inchBigInteger() {
        assertEquals(0.5f, DxaUtils.dxa2inch(BigInteger.valueOf(720)), 0.001f);
    }

    @Test
    public void testDxa2mm() {
        assertEquals(12.7f, DxaUtils.dxa2mm(720.0f), 0.1f);
    }

    @Test
    public void testDxa2mmBigInteger() {
        assertEquals(12.7f, DxaUtils.dxa2mm(BigInteger.valueOf(720)), 0.1f);
    }

    @Test
    public void testEmu2points() {
        assertEquals(1.0f, DxaUtils.emu2points(635 * 20), 0.001f);
    }
}
