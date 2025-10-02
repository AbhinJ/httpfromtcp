package com.jain.abhinav.http.request.pojo;

public record RequestLine(String method, String requestTarget, String httpVersion) {
}
