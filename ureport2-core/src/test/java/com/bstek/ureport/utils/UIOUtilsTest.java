package com.bstek.ureport.utils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.*;

public class UIOUtilsTest {

    @Test
    public void testToByteArray() throws IOException {
        byte[] data = "hello".getBytes("UTF-8");
        byte[] result = UIOUtils.toByteArray(new ByteArrayInputStream(data));
        assertArrayEquals(data, result);
    }

    @Test
    public void testToByteArrayEmpty() throws IOException {
        byte[] result = UIOUtils.toByteArray(new ByteArrayInputStream(new byte[0]));
        assertEquals(0, result.length);
    }

    @Test
    public void testCloseQuietlyInputStream() {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        UIOUtils.closeQuietly(is);
        UIOUtils.closeQuietly((InputStream) null);
    }

    @Test
    public void testCloseQuietlyOutputStream() {
        OutputStream os = new ByteArrayOutputStream();
        UIOUtils.closeQuietly(os);
        UIOUtils.closeQuietly((OutputStream) null);
    }

    @Test
    public void testCopy() throws IOException {
        byte[] data = "test data".getBytes("UTF-8");
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int count = UIOUtils.copy(in, out);
        assertEquals(data.length, count);
        assertArrayEquals(data, out.toByteArray());
    }

    @Test
    public void testCopyEmpty() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int count = UIOUtils.copy(in, out);
        assertEquals(0, count);
    }

    @Test
    public void testWrite() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        UIOUtils.write("test", out, "UTF-8");
        assertEquals("test", out.toString("UTF-8"));
    }

    @Test
    public void testWriteNull() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        UIOUtils.write(null, out, "UTF-8");
        assertEquals(0, out.size());
    }

    @Test
    public void testToInputStream() throws IOException {
        InputStream is = UIOUtils.toInputStream("abc", "UTF-8");
        byte[] data = UIOUtils.toByteArray(is);
        assertEquals("abc", new String(data, "UTF-8"));
    }

    @Test
    public void testToInputStreamNull() throws IOException {
        InputStream is = UIOUtils.toInputStream(null, "UTF-8");
        assertEquals(0, is.available());
    }
}
