package com.bstek.ureport.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ResourceTest {

    @Test
    public void testResource() {
        Resource res = new Resource("path/to/key");
        assertEquals("path/to/key", res.getKey());
    }
}
