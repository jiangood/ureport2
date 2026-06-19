package com.bstek.ureport.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.io.ByteArrayInputStream;

public class UIOUtils {
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    public static void closeQuietly(InputStream input) {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeQuietly(OutputStream output) {
        if (output != null) {
            try {
                output.close();
            } catch (IOException e) {
            }
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        int count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void write(String data, OutputStream output, String encoding) throws IOException {
        if (data != null) {
            output.write(data.getBytes(Charset.forName(encoding)));
        }
    }

    public static InputStream toInputStream(String input, String encoding) throws IOException {
        if (input == null) {
            return new ByteArrayInputStream(new byte[0]);
        }
        return new ByteArrayInputStream(input.getBytes(Charset.forName(encoding)));
    }
}
