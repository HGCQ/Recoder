package yuhan.hgcq.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/* AWS 사용 전 서버 외부 디렉터리 설정 파일 */

@Configuration
public class PhotoConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("/app/images/");

        registry.addResourceHandler("/temp/**")
                .addResourceLocations("/app/temp/");
    }
}