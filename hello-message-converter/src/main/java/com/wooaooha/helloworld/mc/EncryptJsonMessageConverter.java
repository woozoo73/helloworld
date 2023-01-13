package com.wooaooha.helloworld.mc;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
            String source = FileCopyUtils.copyToString(new InputStreamReader(inputMessage.getBody()));
            String decrypted = decrypt(source);
            System.err.println(decrypted);
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

    private String encrypt(String source) {
        return "<ENC>" + source + "<ENC>";
    }

    private String decrypt(String source) {
        return source.replaceAll("<ENC>", "");
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        List<MediaType> mediaTypes = super.getSupportedMediaTypes();
        List<MediaType> appendedTypes = new ArrayList<>();
        appendedTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        appendedTypes.add(MediaType.TEXT_PLAIN);
        appendedTypes.addAll(mediaTypes);

        return appendedTypes;
    }

}
