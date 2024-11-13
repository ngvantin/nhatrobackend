package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.AuthenticationRequest;
import com.example.nhatrobackend.DTO.AuthenticationResponse;
import com.example.nhatrobackend.Entity.Account;
import com.example.nhatrobackend.Responsitory.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        Optional<Account> optionalAccount = accountRepository.findByPhoneNumber(request.getPhoneNumber());
        if(optionalAccount.isPresent()){
            Account account = optionalAccount.get();
            if(passwordEncoder.matches(request.getPassword(),account.getPassword())){
                return AuthenticationResponse.builder()
                        .authenticated(true)
                        .build();
            } else{
                throw new IllegalArgumentException("Mật khẩu không chính xác");
            }
        } else{
            throw new EntityNotFoundException("Số điện thoại chưa đăng ký " + request.getPhoneNumber());

        }
    }

}
