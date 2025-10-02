package com.jain.abhinav.http.request.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ChunkInputStream extends InputStream {
    private final byte[] data;
    private final int numBytesPerRead;
    private int pos = 0;

    public ChunkInputStream(String data, int numBytesPerRead) {
        this.data = data.getBytes(StandardCharsets.UTF_8);
        this.numBytesPerRead = numBytesPerRead;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (pos >= data.length) {
            return -1;
        }
        int remainingBytes = data.length - pos;
        int bytesToRead = Math.min(len, numBytesPerRead);
        bytesToRead = Math.min(bytesToRead, remainingBytes);

        System.arraycopy(this.data, pos, b, off, bytesToRead);

        pos += bytesToRead;

        return bytesToRead;
    }

    @Override
    public int read() throws IOException {
        if (pos >= data.length) {
            return -1;
        }
        return data[pos++] & 0xFF;
    }
}