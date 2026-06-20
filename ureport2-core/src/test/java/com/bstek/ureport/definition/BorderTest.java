package com.bstek.ureport.definition;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BorderTest {

    @Test
    public void testDefaults() {
        Border border = new Border();
        assertEquals(0, border.getWidth());
        assertNull(border.getColor());
        assertNull(border.getStyle());
    }

    @Test
    public void testGettersSetters() {
        Border border = new Border();
        border.setWidth(2);
        border.setColor("#FF0000");
        border.setStyle(BorderStyle.solid);
        assertEquals(2, border.getWidth());
        assertEquals("#FF0000", border.getColor());
        assertEquals(BorderStyle.solid, border.getStyle());
    }
}
