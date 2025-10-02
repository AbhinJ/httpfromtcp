package com.jain.abhinav.http.request;

import com.jain.abhinav.http.exceptions.HttpParseException;
import com.jain.abhinav.http.request.pojo.Request;
import com.jain.abhinav.http.request.utils.ChunkInputStream;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RequestTest {
    @Test
    void testGoodGetRequestLine() {
        String rawRequest = "GET / HTTP/1.1\r\nHost: localhost:42069\r\n\r\n";
        try (InputStream inputStream = new ChunkInputStream(rawRequest, 1)) {
            Request request = assertDoesNotThrow(() -> RequestParser.requestReader(inputStream));

            assertThat(request).isNotNull();
            assertThat(request.getRequestLine().method()).isEqualTo("GET");
            assertThat(request.getRequestLine().requestTarget()).isEqualTo("/");
            assertThat(request.getRequestLine().httpVersion()).isEqualTo("1.1");
        } catch (IOException e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }

    @Test
    void testGoodGetRequestLineWithPath() {
        String rawRequest = "GET /coffee HTTP/1.1\r\nHost: localhost:42069\r\n\r\n";
        try (InputStream inputStream = new ChunkInputStream(rawRequest, 10)) {
            Request request = assertDoesNotThrow(() -> RequestParser.requestReader(inputStream));

            assertThat(request).isNotNull();
            assertThat(request.getRequestLine().method()).isEqualTo("GET");
            assertThat(request.getRequestLine().requestTarget()).isEqualTo("/coffee");
            assertThat(request.getRequestLine().httpVersion()).isEqualTo("1.1");
        } catch (IOException e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }

    @Test
    void testInvalidNumberOfPartsInRequestLine() {
        String rawRequest = "/coffee HTTP/1.1\r\nHost: localhost:42069\r\n\r\n";
        try (InputStream inputStream = new ChunkInputStream(rawRequest, 1)) {
            assertThatThrownBy(() -> RequestParser.requestReader(inputStream))
                    .isInstanceOf(HttpParseException.class)
                    .hasMessage("Request Line must have 3 parts");
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
}
