package com.jain.abhinav.utils;

public class Utils {
    public static int getCRLFIndex(byte[] buffer, int dataEndIndex) {
        int crlfIndex = -1;
        for (int i = 0; i < dataEndIndex - 1; i++) {
            if (buffer[i] == '\r' && buffer[i + 1] == '\n') {
                crlfIndex = i;
                break;
            }
        }
        return crlfIndex;
    }
}
