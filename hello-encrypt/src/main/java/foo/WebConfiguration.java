package foo;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

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
