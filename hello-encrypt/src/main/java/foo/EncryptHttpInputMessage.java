package foo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.StreamUtils;

import java.io.InputStream;

/**
 * 읽기를 가로채기 위해 대신 전달합니다.
 */
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
