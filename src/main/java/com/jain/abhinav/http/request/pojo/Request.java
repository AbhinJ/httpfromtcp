package com.jain.abhinav.http.request.pojo;

import com.jain.abhinav.http.exceptions.HttpParseException;
import java.nio.charset.StandardCharsets;

public class Request {
    private RequestLine requestLine;
    private ParseState state;

    public Request() {
        this.state = ParseState.INITIALISED;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public ParseState getState() {
        return state;
    }

    public int parseRequestLine(byte[] buffer, int dataEndIndex) throws HttpParseException {
        if (state == ParseState.DONE) {
            throw new IllegalStateException("Parser is already in a done state.");
        }
        if (state == ParseState.INITIALISED) {
            int crlfIndex = -1;
            for (int i = 0; i < dataEndIndex - 1; i++) {
                if (buffer[i] == '\r' && buffer[i + 1] == '\n') {
                    crlfIndex = i;
                    break;
                }
            }
            if (crlfIndex == -1) {
                return -1;
            }
            String requestLine = new String(buffer, 0, crlfIndex, StandardCharsets.UTF_8);
            String[] parts = requestLine.split(" ", -1);
            if (parts.length != 3) {
                throw new HttpParseException("Request Line must have 3 parts");
            }

            if(parts[0] == null || parts[0].isEmpty()) throw new HttpParseException("No Method Provided");

            String[] httpVersion = parts[2].split("/", -1);

            if(!httpVersion[0].equals("HTTP")) throw new HttpParseException("It is not HTTP!");

            this.requestLine = new RequestLine(parts[0], parts[1], httpVersion[1]);
            this.state = ParseState.DONE;
            return crlfIndex + 2;
        }
        return 0;
    }

    public int parse(byte[] buffer, int dataEndIndex) throws HttpParseException {
        int read = 0;
        main_loop:
        while (true) {
            switch (this.state){
                case INITIALISED -> {
                    int bytesRead = parseRequestLine(buffer, dataEndIndex);
                    if(bytesRead <= 0){
                        break main_loop;
                    }
                    read += bytesRead;
                }
                case DONE -> {
                    break main_loop;
                }
            }
        }
        return read;
    }

    @Override
    public String toString () {
        return requestLine.toString();
    }
}
