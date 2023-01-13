# 암호화된 요청 복호화하기

* 요청은 아래와 같이 Body 에 value, key 파라미터로 전송된다고 가정합니다.

```http request
POST /greeting HTTP/1.1
Content-Type: application/x-www-form-urlencoded

value=<ENC>{"id": 1, "content": "bar"}<ENC>&key=<ENC>
```

```http request
POST /greeting HTTP/1.1
Content-Type: application/x-www-form-urlencoded

value=<FOO>{"id": 1, "content": "bar"}<FOO>&key=<FOO>
```

* Controller 규격은 아래와 같다고 가정합니다.

```java
@RestController
public class GreetingController {

    @PostMapping("/greeting")
    public Greeting greeting(@RequestBody Greeting greeting) {
        return greeting;
    }

}
```

* 더미 구현에서는 key 값 문자열에 해당하는 암호화된 원문을 지우는 것으로 복호화합니다.
* 즉 위의 요청들은 일반적(정상적?)으로 아래 요청과 같습니다.

```http request
POST /greeting HTTP/1.1
Content-Type: application/json

{"id": 1, "content": "bar"}
```

`Content-Type` `application/x-www-form-urlencoded` 을 Json message converter 가 지원하기 위해

```java
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jsonConverter = null;
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                jsonConverter = (MappingJackson2HttpMessageConverter) converter;
                break;
            }
        }
        if (jsonConverter != null) {
            MappingJackson2HttpMessageConverter customConverter = new EncryptJsonMessageConverter(jsonConverter);
            converters.add(0, customConverter);
        }
    }

}
```

* 아래와 같이 암호화된 Body 를 복호화하여 부모 converter 에 위임하는 형태로 읽기를 구현합니다.

```java
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
```

* PostMan 에서는 아래와 같이 요청합니다.

![](greeting.png)
