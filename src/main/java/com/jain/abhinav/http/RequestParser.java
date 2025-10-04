package com.jain.abhinav.http;

import com.jain.abhinav.http.exceptions.HttpParseException;

import java.io.IOException;
import java.io.InputStream;

public class RequestParser {
    private static final int INITIAL_BUFFER = 1024;
    public static Request requestReader(InputStream inputStream) throws HttpParseException, IOException {
        Request request = new Request();

        byte[] buffer = new byte[INITIAL_BUFFER];
        int dataEndIndex = 0;

        while(request.getState() != ParseState.DONE) {
            int bytesRead = inputStream.read(buffer, dataEndIndex, buffer.length - dataEndIndex);
            if (bytesRead == -1) {
                throw new HttpParseException("Incomplete request line: stream ended unexpectedly.");
            }
            dataEndIndex += bytesRead;
            int bytesConsumed = request.parse(buffer, dataEndIndex);

            if(bytesConsumed > 0) {
                int remainingBytes = dataEndIndex - bytesConsumed;
                System.arraycopy(buffer, bytesConsumed, buffer, 0, remainingBytes);
                dataEndIndex = remainingBytes;
            }
        }
        return request;
    }
}
