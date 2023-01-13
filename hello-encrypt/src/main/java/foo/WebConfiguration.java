package foo;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    /**
     * EncryptJsonMessageConverter 를 등록합니다.
     *
     * @param converters 기본 설정된 메시지 변환기들.
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter customConverter = new EncryptJsonMessageConverter();
        // 순서가 중요합니다. 맨 앞쪽에 두지 않으면, 상태에 따라 다른 변환기가 먼저 처리할 수 있습니다.
        converters.add(0, customConverter);
    }

}
