package com.bstek.ureport.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ImageTest {

    @Test
    public void testImage() {
        Image img = new Image("base64data", 100, 200);
        assertEquals("base64data", img.getBase64Data());
        assertEquals(100, img.getWidth());
        assertEquals(200, img.getHeight());
        assertNull(img.getPath());
    }

    @Test
    public void testImageWithPath() {
        Image img = new Image("base64data", "/path/img.png", 100, 200);
        assertEquals("base64data", img.getBase64Data());
        assertEquals("/path/img.png", img.getPath());
        assertEquals(100, img.getWidth());
        assertEquals(200, img.getHeight());
    }
}
