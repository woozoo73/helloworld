package foo;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 읽기와 쓰기를 가로채고, 실제 진지한 쓰기 구현은 MappingJackson2HttpMessageConverter 기본 구현을 따릅니다.
 * 다른 미디어 형식을 지원합니다.
 *
 * @see #getSupportedMediaTypes()
 */
public class EncryptJsonMessageConverter extends MappingJackson2HttpMessageConverter {

    @Override
    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        if (inputMessage.getBody() == null) {
            return super.read(type, contextClass, inputMessage);
        }

        // 요청 스트림(request body)을 바로 읽으면, 규격이 안 맞기 때문에, 가로챈 다음 규격에 맞는 요청 스트림의 내용으로 바꿉니다.
        // 암호화된 JSON & 키 --> 평문 JSON
        String decrypted = decrypt();
        HttpInputMessage decryptedMessage = new EncryptHttpInputMessage(inputMessage.getHeaders(), new ByteArrayInputStream(decrypted.getBytes(StandardCharsets.UTF_8)));

        return super.read(type, contextClass, decryptedMessage);
    }

    @Override
    public void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotReadableException {
        // 응답 스트림(response body)에 바로 쓰면, 규격이 안 맞고, 쓰기를 직접 구현하는 것은 번거롭기 때문에 쓰는 동작을 가로채기 위한 스트림을 대신 전달합니다.
        ByteArrayOutputStream spy = new ByteArrayOutputStream();
        EncryptHttpOutputMessage interceptor = new EncryptHttpOutputMessage(outputMessage.getHeaders(), spy);

        // 쓰기 동작을 위임합니다.
        super.writeInternal(object, type, interceptor);

        // 가로채어진 내용을 규격에 맞는 내용으로 바꿉니다.
        // 평문 JSON --> 암호화된 JSON & 키
        String origin = new String(spy.toByteArray(), StandardCharsets.UTF_8);
        String encrypted = encrypt(origin);

        // 암호문 & 키를 응답 스트림에 씁니다.
        outputMessage.getBody().write(encrypted.getBytes(StandardCharsets.UTF_8));
        outputMessage.getBody().flush();
    }

    /**
     * 평문 JSON 을 반환합니다.
     *
     * @return 평문 JSON.
     */
    private String decrypt() {
        // 요청 정보를 얻습니다.
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 요청 파라미터를 통해 암호화된 JSON 과 키를 얻습니다.
        // application/x-www-form-urlencoded 이기 때문에 요청 파라미터를 통해 이들을 얻을 수 있습니다.
        String value = request.getParameter("value");
        String key = request.getParameter("key");

        return EncryptUtils.decrypt(value, key);
    }

    /**
     * 암호화된 JSON & 키 조합을 반환합니다.
     *
     * @param origin 평문 JSON.
     * @return 암호화된 JSON & 키 조합.
     */
    private String encrypt(String origin) {
        String key = EncryptUtils.generateKey();

        return EncryptUtils.valueAndKey(origin, key);
    }

    /**
     * 기본적으로 application/json 만 처리하기 때문에, application/x-www-form-urlencoded 형식을 추가합니다.
     *
     * @return 지원하는 미디어 형식.
     */
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        List<MediaType> mediaTypes = super.getSupportedMediaTypes();
        List<MediaType> appendedTypes = new ArrayList<>();
        appendedTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        appendedTypes.addAll(mediaTypes);

        return appendedTypes;
    }

}
