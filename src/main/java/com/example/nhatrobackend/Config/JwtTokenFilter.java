//package com.example.nhatrobackend.Config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class JwtTokenFilter extends OncePerRequestFilter {
//
//    private final JwtDecoder jwtDecoder;
//
//    public JwtTokenFilter(JwtDecoder jwtDecoder) {
//        this.jwtDecoder = jwtDecoder;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        // Lấy JWT từ cookie
//        String jwtToken = extractJwtFromCookie(request);
//
//        if (StringUtils.hasText(jwtToken)) {
//            try {
//                var jwt = jwtDecoder.decode(jwtToken); // Giải mã và xác thực JWT
//                var authentication = new JwtAuthenticationConverter().convert(jwt);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            } catch (Exception ex) {
//                // Nếu token không hợp lệ, xóa ngữ cảnh bảo mật
//                SecurityContextHolder.clearContext();
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private String extractJwtFromCookie(HttpServletRequest request) {
//        if (request.getCookies() != null) {
//            for (Cookie cookie : request.getCookies()) {
//                if ("jwtToken".equals(cookie.getName())) {
//                    return cookie.getValue();
//                }
//            }
//        }
//        return null;
//    }
//}
//
