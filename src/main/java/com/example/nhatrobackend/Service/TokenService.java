package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.Entity.Token;
import com.example.nhatrobackend.Responsitory.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public record TokenService(TokenRepository tokenRepository) {

    public Token getByPhoneNumber(String phoneNumber) {
        return tokenRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new RuntimeException("Not found token"));
    }

    public int save(Token token) {
        Optional<Token> optional = tokenRepository.findByPhoneNumber(token.getPhoneNumber());
        if(optional.isEmpty()){
            tokenRepository.save(token);
            return token.getId();
        }
        else{
            Token t = optional.get();
            t.setAccessToken(token.getAccessToken());
            t.setRefreshToken(token.getRefreshToken());
            tokenRepository.save(token);
            return t.getId();
        }
    }

    public void delete(String phoneNumber) {
        Token token = getByPhoneNumber(phoneNumber);
        tokenRepository.delete(token);
    }
}
