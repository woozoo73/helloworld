package foo;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EncryptJsonMessageConverter extends MappingJackson2HttpMessageConverter {

    MappingJackson2HttpMessageConverter converter;

    public EncryptJsonMessageConverter(MappingJackson2HttpMessageConverter converter) {
        this.converter = converter;
    }

    @Override
    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) {
        try {
            if (inputMessage.getBody() == null) {
                return super.read(type, contextClass, inputMessage);
            }

            String decrypted = getBody();
            HttpInputMessage decryptedMessage = new EncryptHttpInputMessage(inputMessage.getHeaders(), new ByteArrayInputStream(decrypted.getBytes(StandardCharsets.UTF_8)));
            return super.read(type, contextClass, decryptedMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) {
        try {
            super.writeInternal(object, type, outputMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getBody() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String value = request.getParameter("value");
        String key = request.getParameter("key");

        return EncryptUtils.decrypt(value, key);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        List<MediaType> mediaTypes = super.getSupportedMediaTypes();
        List<MediaType> appendedTypes = new ArrayList<>();
        appendedTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        appendedTypes.addAll(mediaTypes);

        return appendedTypes;
    }

}
