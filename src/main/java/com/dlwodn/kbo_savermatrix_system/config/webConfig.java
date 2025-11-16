package com.dlwodn.kbo_savermatrix_system.config; // (config 패키지 맞는지 확인!)

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // "이 파일은 '설정' 파일입니다"라고 스프링에게 알려줌
public class webConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // "이 '엔진'의 '모든 주소'(/api/**)에 대해서..."
        registry.addMapping("/api/**")
                // "...'저기 저 손님'(http://localhost:5173)에게는..."
                .allowedOrigins("http://localhost:5173")
                // "...'모든 방식'(GET, POST 등)의 '전화'를 '허용'해 드려라!"
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}