package com.example.nhatrobackend.Sercurity;

import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Responsitory.UserRepository;
import com.example.nhatrobackend.Service.UserService;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

//@Component
//public class AuthenticationFacadeImpl2 implements AuthenticationFacade {
//
//    @Override
//    public String getCurrentUserUuid() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            return authentication.getName(); // Lấy userUuid từ `Authentication.getName()`
//        }
//        throw new IllegalStateException("User is not authenticated");
//    }
//
//    @Override
//    public String getCurrentUserUuid(HttpServletRequest request) {
//        return null;
//    }
//}


@Component
@RequiredArgsConstructor
public class AuthenticationFacadeImpl implements AuthenticationFacade {
    private final UserService userService;
    private final UserRepository userRepository;
//    @Override
//    public String getCurrentUserUuid() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            return authentication.getName(); // Lấy userUuid từ `Authentication.getName()`
//        }
//        throw new IllegalStateException("User is not authenticated");
//    }

    @Override
    public String getCurrentUserUuid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Lấy userUuid từ `Authentication.getName()`
            String phoneNumber = authentication.getName(); // Lấy số điện thoại của user hiện tại
            User user = userService.findByPhoneNumber(phoneNumber);
            return user.getUserUuid();
        }
        throw new IllegalStateException("User is not authenticated");
    }

    @Override
    public String getCurrentUserUuidToPhone() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String phoneNumber = authentication.getName();
            Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
            if (userOptional.isPresent()) {
                return userOptional.get().getUserUuid();
            }
        }
        return null;
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

    @Override
    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Lấy userUuid từ `Authentication.getName()`
            String phoneNumber = authentication.getName(); // Lấy số điện thoại của user hiện tại
            User user = userService.findByPhoneNumber(phoneNumber);
            return user.getUserId();
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


