package com.bstek.ureport.build;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BindDataTest {

    @Test
    public void testBindData() {
        BindData bd = new BindData("value");
        assertEquals("value", bd.getValue());
        assertNull(bd.getLabel());
    }

    @Test
    public void testBindDataWithLabel() {
        BindData bd = new BindData("value", "label", null);
        assertEquals("value", bd.getValue());
        assertEquals("label", bd.getLabel());
    }
}
