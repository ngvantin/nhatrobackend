package com.example.nhatrobackend.Config;


import com.example.nhatrobackend.Service.CustomUserDetailsService;
import com.example.nhatrobackend.Service.JwtService;
import com.example.nhatrobackend.Service.UserService;
import com.example.nhatrobackend.util.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

import static com.example.nhatrobackend.util.TokenType.ACCESS_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

// hứng các request trước hợp lệ thì mới chuyền đến bộ lọc Api
@Component // Đánh dấu Bean để Spring quản lý (tự động tạo và inject filter này)
@Slf4j
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter {
    // OncePerRequestFilter: Đảm bảo mỗi request chỉ chạy filter này 1 lần
    private final UserService userService;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    // filter này sẽ đứng trc mọi request api
    // FilterChain: Chuỗi các filters cần thực thi.
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("---------- doFilterInternal ----------");


        final String authorization = request.getHeader(AUTHORIZATION);
        //log.info("Authorization: {}", authorization);

        // Nếu không hợp lệ, thì không làm gì nữa
        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorization.substring("Bearer ".length());
        log.info("Token: {}", token);

        final String userName = jwtService.extractUsername(token,ACCESS_TOKEN);
        // SecurityContextHolder Nó chứa một SecurityContext, trong đó lưu đối tượng Authentication (dữ liệu user đã đăng nhập).
        if (StringUtils.isNotEmpty(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userName); //  tìm user trong database theo tên đăng nhập (userName).
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);            // kiểm tra m JWT token nếu hợp lệ, nó cấp quyền xác thực cho người dùng trong SecurityContextHolder.
            if (jwtService.isValid(token,ACCESS_TOKEN ,userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext(); //SecurityContext lưu trữ thông tin xác thực (Authentication) biết ai đang đăng nhập, có quyền gì, và giúp kiểm tra quyền truy cập tài nguyên.
                // Tạo một đối tượng UsernamePasswordAuthenticationToken để chứa thông tin xác thực của người dùng.
                //userDetails: Chứa thông tin người dùng (username, password, roles).
                //null: Không cần password vì token đã được xác thực.
                //userDetails.getAuthorities(): Danh sách quyền hạn (roles) của người dùng.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authentication); // Gán authentication vào SecurityContext để Spring Security hiểu rằng user này đã đăng nhập.
                SecurityContextHolder.setContext(context);
            }
        }

        filterChain.doFilter(request, response);
    }
}
