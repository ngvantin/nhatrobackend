package com.example.nhatrobackend.Filter;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Builder
@Service
@RequiredArgsConstructor
public class JwtService {
    @NonFinal
    protected static final String signerKey = "ygsK2f77lf3CniPIOEVSrXGc6Tk4OmTfcSNJAZqOgsCXV86fESh2UP1awc66KOsG";


    public String generateToken(String usename)
    {
        JWSHeader header = new JWSHeader(JWSAlgorithm.ES512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(usename)
                .issuer("nhatro.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
    // ký token
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}
