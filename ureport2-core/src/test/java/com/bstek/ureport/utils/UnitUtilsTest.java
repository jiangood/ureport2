package com.bstek.ureport.utils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UnitUtilsTest {

    @Test
    public void testPointToPixel() {
        int result = UnitUtils.pointToPixel(72);
        assertEquals(95, result);
    }

    @Test
    public void testPointToPixelZero() {
        int result = UnitUtils.pointToPixel(0);
        assertEquals(0, result);
    }

    @Test
    public void testPixelToPoint() {
        int result = UnitUtils.pixelToPoint(100);
        assertEquals(75, result);
    }

    @Test
    public void testPixelToPointZero() {
        int result = UnitUtils.pixelToPoint(0);
        assertEquals(0, result);
    }

    @Test
    public void testPointToInche() {
        float result = UnitUtils.pointToInche(72f);
        assertEquals(1.0f, result, 0.001);
    }

    @Test
    public void testPointToTwip() {
        int result = UnitUtils.pointToTwip(10);
        assertEquals(200, result);
    }

    @Test
    public void testPointToTwipZero() {
        int result = UnitUtils.pointToTwip(0);
        assertEquals(0, result);
    }
}
