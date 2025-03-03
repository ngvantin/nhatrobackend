package com.example.nhatrobackend.Service;


import com.example.nhatrobackend.util.TokenType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AccessToken;


public interface JwtService {

    String generateToken(UserDetails user);
    String generateRefreshToken(UserDetails user);
    String generateResetToken(UserDetails user);

//    String generateToken(String usename);
    String extractUsername(String token, TokenType type);

    boolean isValid(String token,TokenType type, UserDetails user);

}
