package com.jain.abhinav.http.requestLine;

import com.jain.abhinav.http.exceptions.HttpParseException;

import java.nio.charset.StandardCharsets;

public class RequestLine {
    private String method;
    private String requestTarget;
    private String httpVersion;

    public int parseRequestLine(byte[] buffer, int offset, int dataEndIndex) throws HttpParseException {
        int crlfIndex = -1;
        for (int i = offset; i < dataEndIndex - 1; i++) {
            if (buffer[i] == '\r' && buffer[i + 1] == '\n') {
                crlfIndex = i;
                break;
            }
        }
        if (crlfIndex == -1) {
            return -1;
        }
        int requestLineLength = crlfIndex - offset;
        String requestLine = new String(buffer, offset, requestLineLength, StandardCharsets.UTF_8);
        String[] parts = requestLine.split(" ", -1);
        if (parts.length != 3) {
            throw new HttpParseException("Request Line must have 3 parts");
        }

        if(parts[0] == null || parts[0].isEmpty()) throw new HttpParseException("No Method Provided");

        String[] httpVersion = parts[2].split("/", -1);

        if(!httpVersion[0].equals("HTTP")) throw new HttpParseException("It is not HTTP!");

        this.method = parts[0];
        this.requestTarget = parts[1];
        this.httpVersion = httpVersion[1];
        return requestLineLength + 2;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method='" + method + '\'' +
                ", requestTarget='" + requestTarget + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                '}';
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public void setRequestTarget(String requestTarget) {
        this.requestTarget = requestTarget;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }
}
