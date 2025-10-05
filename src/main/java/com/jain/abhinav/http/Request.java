package com.jain.abhinav.http;

import com.jain.abhinav.http.exceptions.HttpParseException;
import com.jain.abhinav.http.headers.Headers;
import com.jain.abhinav.http.requestLine.RequestLine;

public class Request {
    private RequestLine requestLine;
    private Headers headers;
    private ParseState state;

    public ParseState getState() {
        return state;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Request() {
        this.state = ParseState.INITIALISED;
        this.requestLine = new RequestLine();
        this.headers = new Headers();
    }

    public int parse(byte[] buffer, int dataEndIndex) throws HttpParseException {
        int read = 0;
        main_loop:
        while (true) {
            switch (this.state){
                case INITIALISED -> {
                    int bytesRead = requestLine.parseRequestLine(buffer, read, dataEndIndex);
                    if(bytesRead <= 0){
                        break main_loop;
                    }
                    read += bytesRead;
                    this.state = ParseState.HEADERS;
                }
                case HEADERS -> {
                    int byteRead = headers.parse(buffer, read, dataEndIndex);
                    if(byteRead <= 0){
                        break main_loop;
                    }
                    read += byteRead;
                    if(headers.isDoneParsing()) this.state = ParseState.DONE;
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
        StringBuilder string = new StringBuilder();
        string
                .append("Request Line")
                .append("\r\n")
                .append(requestLine.toString())
                .append("\r\n")
                .append("Headers")
                .append("\r\n")
                .append(headers.toString());

        return string.toString();
    }

    public Headers getHeaders() {
        return headers;
    }
}
