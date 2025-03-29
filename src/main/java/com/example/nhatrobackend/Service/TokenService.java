package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.Entity.Token;
import com.example.nhatrobackend.Responsitory.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final TokenRepository tokenRepository;

    public Token getByPhoneNumber(String phoneNumber) {
        log.info("getByPhoneNumber for: {}", phoneNumber);
        return tokenRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new RuntimeException("Not found token"));
    }

    @Transactional
    public int save(Token token) {
        String phoneNumber = token.getPhoneNumber();
        log.info("save called for phone number: {}", phoneNumber);
        Optional<Token> optional = tokenRepository.findByPhoneNumber(phoneNumber);
        log.info("findByPhoneNumber result for {}: {}", phoneNumber, optional.isPresent());
        if (optional.isEmpty()) {
            log.info("Saving new token: {}", token);
            Token savedToken = tokenRepository.save(token);
            log.info("New token saved with ID: {}", savedToken.getId());
            return savedToken.getId();
        } else {
            Token existingToken = optional.get();
            existingToken.setAccessToken(token.getAccessToken());
            existingToken.setRefreshToken(token.getRefreshToken());
            log.info("Updating existing token: {}", existingToken);
            Token updatedToken = tokenRepository.save(existingToken);
            log.info("Existing token updated with ID: {}", updatedToken.getId());
            return updatedToken.getId();
        }
    }

    public void delete(String phoneNumber) {
        Token token = getByPhoneNumber(phoneNumber);
        tokenRepository.delete(token);
    }
}
