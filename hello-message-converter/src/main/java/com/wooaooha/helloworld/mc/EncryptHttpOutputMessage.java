package com.wooaooha.helloworld.mc;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;

import java.io.OutputStream;

public class EncryptHttpOutputMessage implements HttpOutputMessage {

    private final HttpHeaders headers;

    private final OutputStream body;

    public EncryptHttpOutputMessage(HttpHeaders headers, OutputStream body) {
        this.headers = headers;
        this.body = body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public OutputStream getBody() {
        return this.body;
    }

}
