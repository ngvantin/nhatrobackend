package com.example.nhatrobackend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Áp dụng cho tất cả các endpoint
                .allowedOrigins("http://localhost:5173")  // Chỉ cho phép yêu cầu từ frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Cho phép các phương thức HTTP
                .allowedHeaders("*")
                .allowCredentials(true);  // Cho phép tất cả các header

    }
}
