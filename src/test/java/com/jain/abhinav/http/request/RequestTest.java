package com.jain.abhinav.http.request;

import com.jain.abhinav.http.Request;
import com.jain.abhinav.http.RequestParser;
import com.jain.abhinav.http.exceptions.HttpParseException;
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
            assertThat(request.getRequestLine().getMethod()).isEqualTo("GET");
            assertThat(request.getRequestLine().getRequestTarget()).isEqualTo("/");
            assertThat(request.getRequestLine().getHttpVersion()).isEqualTo("1.1");
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
            assertThat(request.getRequestLine().getMethod()).isEqualTo("GET");
            assertThat(request.getRequestLine().getRequestTarget()).isEqualTo("/coffee");
            assertThat(request.getRequestLine().getHttpVersion()).isEqualTo("1.1");
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

    @Test
    void testInvalidHeaders() {
        String rawRequest = "GET / HTTP/1.1\r\nHost localhost:42069\r\n\r\n";
        try (InputStream inputStream = new ChunkInputStream(rawRequest, 10)) {
            assertThatThrownBy(() -> RequestParser.requestReader(inputStream))
                    .isInstanceOf(HttpParseException.class)
                    .hasMessage("field not properly formatted");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGoodHeaders() {
        String rawRequest = "GET / HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n";
        try (InputStream inputStream = new ChunkInputStream(rawRequest, 10)) {
            Request request = assertDoesNotThrow(() -> RequestParser.requestReader(inputStream));
            assertThat(request).isNotNull();
            assertThat(request.getHeaders().getHeader("host")).isEqualTo("localhost:42069");
            assertThat(request.getHeaders().getHeader("user-agent")).isEqualTo("curl/7.81.0");
            assertThat(request.getHeaders().getHeader("accept")).isEqualTo("*/*");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDuplicateFields() {
        String rawRequest = "GET / HTTP/1.1\r\n    Host: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n";
        try (InputStream inputStream = new ChunkInputStream(rawRequest, 10)) {
            Request request = assertDoesNotThrow(() -> RequestParser.requestReader(inputStream));
            assertThat(request).isNotNull();
            assertThat(request.getHeaders().getHeader("host")).isEqualTo("localhost:42069");
            assertThat(request.getHeaders().getHeader("user-agent")).isEqualTo("curl/7.81.0,curl/7.81.0");
            assertThat(request.getHeaders().getHeader("accept")).isEqualTo("*/*");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testInvalidFieldExtraSpace() {
        String rawRequest = "GET / HTTP/1.1\r\n   Host  : localhost:42069\r\nUser-Agent: curl/7.81.0\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n";
        try (InputStream inputStream = new ChunkInputStream(rawRequest, 10)) {
            assertThatThrownBy(() -> RequestParser.requestReader(inputStream))
                    .isInstanceOf(HttpParseException.class)
                    .hasMessage("field not properly formatted");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
