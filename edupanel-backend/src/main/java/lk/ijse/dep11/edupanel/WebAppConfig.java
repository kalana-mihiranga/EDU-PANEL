package lk.ijse.dep11.edupanel;

import lk.ijse.dep11.edupanel.converter.LecturerTypeConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan
public class WebAppConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new LecturerTypeConverter());
    }

    @Bean
    public StandardServletMultipartResolver multipartResolver(){
        return new StandardServletMultipartResolver();

    }
}
