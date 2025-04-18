//package com.example.nhatrobackend.Config;
//
//import com.example.nhatrobackend.Entity.Field.UserType;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
//import org.springframework.security.web.SecurityFilterChain;
//
//import javax.crypto.spec.SecretKeySpec;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
////    @Autowired
////    private JWTFilter jwtFilter;
//    @Value("${jwt.signerKey}")
//    protected String signerKey;
//    public final String[] PUBLIC_ENDPOINTS = {"/api/post",
//            "/api/post/search",
//            "/api/post/detail/{postUuid}",
//            "/api/post/filter",
//            "/api/post/{postUuid}/user",
//            "/api/auth/**",
//            "/api/administrative/**"};
//
//
////    @Bean
////    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, JwtTokenFilter jwtTokenFilter) throws Exception {
////        httpSecurity.csrf(AbstractHttpConfigurer::disable);
////
////        httpSecurity
////                .authorizeHttpRequests(request -> request
////                        .requestMatchers(
////                                "/api/post",
////                                "/api/post/search",
////                                "/api/post/upload",
////                                "/api/post/detail/{postUuid}",
////                                "/api/post/filter",
////                                "/api/post/{postUuid}/user",
////                                "/api/auth/**",
////                                "/api/administrative/**"
////                        ).permitAll()
//////                        .anyRequest().authenticated()
////                        .anyRequest().permitAll()
////                );
////
////        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
////
////        return httpSecurity.build();
////    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.csrf(AbstractHttpConfigurer::disable); // Vô hiệu hóa CSRF cho REST API
//
//        httpSecurity
//                .authorizeHttpRequests(request -> request
//                                // Các API không cần xác thực
//                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
//                                .requestMatchers("/api/post/admin/**").hasRole(UserType.ADMIN.name())
////                                .requestMatchers("/api/post/admin/**").hasAuthority("ROLE_ADMIN")
//                                // Các request khác mặc định yêu cầu xác thực
//                        .anyRequest().authenticated()
////                                .anyRequest().permitAll()
//
//                );
//
//        //dk 1 provider
//        httpSecurity.oauth2ResourceServer(oauth2 ->
//                oauth2.jwt(jwtConfigurer ->
//                        jwtConfigurer.decoder(jwtDecoder())
//                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
//        );
//
//
//        return httpSecurity.build();
//    }
//
//    @Bean
//    JwtDecoder jwtDecoder(){
//        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(),"HS512");
//        return NimbusJwtDecoder
//                .withSecretKey(secretKeySpec)
//                .macAlgorithm(MacAlgorithm.HS512)
//                .build();
//    }
//
//    @Bean
//    JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
//        return jwtAuthenticationConverter;
//    }
//
//
//
//}
//
