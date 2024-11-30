package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.AuthenticationRequest;
import com.example.nhatrobackend.DTO.AuthenticationResponse;
import com.example.nhatrobackend.DTO.IntrospectRequest;
import com.example.nhatrobackend.DTO.IntrospectResponse;
import com.example.nhatrobackend.Entity.Account;
import com.example.nhatrobackend.Responsitory.AccountRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

//    @NonFinal
//    protected static final String SIGNER_KEY = "sXUTpDGAiL9kVkdE7jspTrpYZ3pQeHdaKBAKczxpkqJ/Wk83qgdkld/jhzFf7vy2";

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        Optional<Account> optionalAccount = accountRepository.findByPhoneNumber(request.getPhoneNumber());
        if(optionalAccount.isPresent()){
            Account account = optionalAccount.get();
            if(passwordEncoder.matches(request.getPassword(),account.getPassword())){
                var token = generateToken(request.getPhoneNumber());
                return AuthenticationResponse.builder()
                        .accessToken(token)
                        .authenticated(true)
                        .build();
            } else{
                throw new IllegalArgumentException("Mật khẩu không chính xác");
            }
        } else{
            throw new EntityNotFoundException("Số điện thoại chưa đăng ký " + request.getPhoneNumber());

        }
    }
    private String generateToken(String phoneNumber){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject (phoneNumber)
                .issuer("example.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                Instant.now().plus( 1, ChronoUnit.DAYS).toEpochMilli()
                ))
                .claim( "custonClaim", "Custom")
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header,payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

    }

    public IntrospectResponse introspect (IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();
    }

}
