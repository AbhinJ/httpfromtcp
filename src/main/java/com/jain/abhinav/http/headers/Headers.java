package com.jain.abhinav.http.headers;

import com.jain.abhinav.http.exceptions.HttpParseException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class Headers {
    private Map<String, String> headers = new HashMap<>();
    private boolean isDoneParsing = false;

    public String getHeader(String key) {
        return headers.get(key.toLowerCase());
    }

    public void setHeader(String key, String value) {
        String name = key.toLowerCase();
        if (headers.containsKey(name)) {
            String v = getHeader(name);
            value = v + "," + value;
            headers.put(name, value);
        } else {
            headers.put(name, value);
        }
    }

    public boolean isValidField(String name) {
        String regex = "^[A-Za-z0-9!#$%&'*+-.^_`|~]+$";
        return name.matches(regex);
    }

    public void parseAndStoreHeader(String header) throws HttpParseException {
        String[] parts = header.split(":", 2);
        if (parts.length != 2) {
            throw new HttpParseException("Header not properly formatted");
        }
        String name = parts[0].stripLeading();
        if (!isValidField(name)) {
            throw new HttpParseException("field not properly formatted");
        }
        String value = parts[1].strip();
        setHeader(name, value);
    }

    public int parse(byte[] buffer, int offset, int dataEndIndex) throws HttpParseException {
        int read = offset;
        while(true) {
            int crlfIndex = -1;
            for (int i = read; i < dataEndIndex - 1; i++) {
                if (buffer[i] == '\r' && buffer[i + 1] == '\n') {
                    crlfIndex = i;
                    break;
                }
            }
            if (crlfIndex == -1) {
                break;
            }
            if (crlfIndex == read) {
                read += 2;
                isDoneParsing = true;
                break;
            }
            String header = new String(buffer, read, crlfIndex - read, StandardCharsets.UTF_8);
            parseAndStoreHeader(header);
            read = crlfIndex + 2;
        }
        return read - offset;
    }

    public boolean isDoneParsing() {
        return isDoneParsing;
    }
}
