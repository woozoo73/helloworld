package com.wooaooha.helloworld.mc;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.StreamUtils;

import java.io.InputStream;

public class EncryptHttpInputMessage implements HttpInputMessage {

    private final HttpHeaders headers;

    private final InputStream body;

    public EncryptHttpInputMessage(HttpHeaders headers, InputStream body) {
        this.headers = headers;
        this.body = body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public InputStream getBody() {
        return (this.body != null ? this.body : StreamUtils.emptyInput());
    }

}
