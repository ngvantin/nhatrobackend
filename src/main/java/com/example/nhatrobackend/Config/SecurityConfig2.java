package com.example.nhatrobackend.Config;

import com.example.nhatrobackend.Service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration // Đánh dấu đây là một class cấu hình Spring, chứa các @Bean để cấu hình bảo mật.
@Profile("!prod") // Đoạn cấu hình này chỉ chạy khi môi trường không phải production.
@RequiredArgsConstructor //  tự động tạo một constructor chứa các final fields và  @NonNull
public class SecurityConfig2 {

    private final UserService userService;
    private final PreFilter preFilter;

    private String[] WHITE_LIST = {"/api/post",
            "/api/post/search",
            "/api/post/detail/{postUuid}",
            "/api/post/filter",
            "/api/post/{postUuid}/user",
            "/api/auth/**",
            "/api/administrative/**"};


    // chuỗi các bộ lọc bảo mật
    // "throws Exception" phương thức có thể ném ngoại lệ, bắt buộc người gọi xử lý hoặc khai báo ngoại lệ.
    // AbstractHttpConfigurer::disable -> Trong RESTful API (đặc biệt khi dùng JWT), CSRF thường không cần thiết, vì không sử dụng session-based authentication.
    // STATELESS -> STATELESS cấu hình ứng dụng để không sử dụng session (cookie-based session) mà dùng  JWT hoặc token.
    // authenticationProvider(provider()) Đăng ký một AuthenticationProvider tùy chỉnh để xử lý xác thực người dùng.
    // .addFilterBefore(preFilter, UsernamePasswordAuthenticationFilter.class); -> do Before nên  bộ lọc preFilter xảy ra trước UsernamePasswordAuthenticationFilter
    @Bean
    public SecurityFilterChain configure(@NonNull HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers(WHITE_LIST).permitAll().anyRequest().permitAll())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(provider()).addFilterBefore(preFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Loại bỏ một số URL khỏi Spring Security filter chain của Swagger
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return webSecurity ->
                webSecurity.ignoring()
                        .requestMatchers("/actuator/**", "/v3/**", "/webjars/**", "/swagger-ui*/*swagger-initializer.js", "/swagger-ui*/**");
    }

    // AuthenticationManager để xử lý xác thực đăng nhập.dựa trên các AuthenticationProvider đã được cấu hình. quản lý các role các user đăng nhập
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider provider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService.userDetailsService());
        provider.setPasswordEncoder(getPasswordEncoder());

        return provider;
    }
}
