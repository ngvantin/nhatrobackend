package com.example.nhatrobackend.Sercurity;

import com.example.nhatrobackend.Filter.JwtService;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//@Component
//public class AuthenticationFacadeImpl implements AuthenticationFacade {
//
//    @Override
//    public String getCurrentUserUuid() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            return authentication.getName(); // Lấy userUuid từ `Authentication.getName()`
//        }
//        throw new IllegalStateException("User is not authenticated");
//    }
//}


@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade {
        @Override
    public String getCurrentUserUuid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Lấy userUuid từ `Authentication.getName()`
        }
        throw new IllegalStateException("User is not authenticated");
    }

    @Override
    public String getCurrentUserUuid(HttpServletRequest request) {
        // Lấy JWT token từ cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    String jwtToken = cookie.getValue();
                    return getUserUuidFromToken(jwtToken); // Giải mã JWT token để lấy userUuid
                }
            }
        }
        throw new IllegalStateException("User is not authenticated");
    }

    private String getUserUuidFromToken(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            JWTClaimsSet claims = JWTClaimsSet.parse(jwsObject.getPayload().toString());
            return claims.getSubject(); // Giải mã và lấy userUuid từ token
        } catch (Exception e) {
            throw new IllegalStateException("Invalid token", e);
        }
    }
}


