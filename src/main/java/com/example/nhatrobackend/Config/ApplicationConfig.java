package com.example.nhatrobackend.Config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }



    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**") // Cho phép tất cả API
                        .allowedOrigins("http://localhost:5173") // Cho phép frontend truy cập
//                        .allowedOrigins("http://localhost:5173/") // Cho phép frontend truy cập
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Các phương thức HTTP
                        .allowedHeaders("*") // Chấp nhận tất cả các header
                        .allowCredentials(true) // Nếu cần gửi cookie hoặc xác thực
                        .maxAge(3600); // Cache CORS 1 giờ
            }
        };
    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(@NonNull CorsRegistry registry) {
//                registry.addMapping("**")
//                        .allowedOrigins("http://localhost:5173")
//                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed HTTP methods
//                        .allowedHeaders("*") // Allowed request headers
//                        .allowCredentials(false)
//                        .maxAge(3600);
//            }
//        };
//    }

}
