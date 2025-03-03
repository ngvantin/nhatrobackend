package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.Service.JwtService;

import com.example.nhatrobackend.util.TokenType;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.example.nhatrobackend.util.TokenType.*;

//@Builder
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @NonFinal
    protected static final String signerKey = "ygsK2f77lf3CniPIOEVSrXGc6Tk4OmTfcSNJAZqOgsCXV86fESh2UP1awc66KOsG";

    @Value("${jwt.expiryHour}")
    private long expiryHour;

    @Value("${jwt.expiryDay}")
    private long expiryDay;

    @Value("${jwt.accessKey}")
    private String accessKey; //  dùng để ký và xác thực JWT

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Value("${jwt.resetKey}")
    private String resetKey;

    @Override
    public String generateToken(UserDetails user) {
        return generateToken(new HashMap<>(), user); // HashMap được sử dụng để chứa các claims
    }

    @Override
    public String generateRefreshToken(UserDetails user) {

        return generateRefreshToken(new HashMap<>(), user);
    }

    @Override
    public String generateResetToken(UserDetails user) {

        return generateResetToken(new HashMap<>(), user);
    }

    // Lấy username từ JWT.
    @Override
    public String extractUsername(String token, TokenType type) {
        return extractClaim(token, type,Claims::getSubject);
        // Claims::getSubject tương đương với Lambda Expression: claims -> claims.getSubject()
    }

    //  Kiểm tra xem token có hợp lệ
    @Override
    public boolean isValid(String token,TokenType type, UserDetails userDetails) {
        final String username = extractUsername(token,type); // Lấy username từ token.
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, type));
    }


    private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // Thời điểm phát hành token là thời điểm hiện tại.
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expiryHour))
                .signWith(getKey(ACCESS_TOKEN), SignatureAlgorithm.HS256) // Ký token bằng khóa bí mật và thuật toán HS256.
                .compact(); // Xây dựng và trả về chuỗi JWT.
    }

    private String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails) {

        log.info("---------- generateRefreshToken JwtServiceImpl ----------");
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay))
                .signWith(getKey(REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateResetToken(Map<String, Object> claims, UserDetails userDetails) {

        log.info("---------- generateResetToken JwtServiceImpl ----------");
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getKey(RESET_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }


    private Key getKey(TokenType type) {
        log.info("---------- getKey JwtServiceImpl ----------");
        switch (type){
            case ACCESS_TOKEN ->{
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));

            }
            case REFRESH_TOKEN ->{
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            }
            case RESET_TOKEN ->{
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(resetKey));
            }
            default -> throw new IllegalArgumentException("Invalid token type");

        }
//        byte[] keyBytes = Decoders.BASE64.decode(accessKey); // giải mã khóa bí mật thành mảng byte
//        return Keys.hmacShaKeyFor(keyBytes); // tạo một khóa bí mật (Key) từ mảng byte
    }

    //lấy một claim bất kỳ từ JWT.
    // <T>:  một kiểu generic. cho phéplàm việc với nhiều kiểu dữ liệu khác nhau
    // T: Kiểu dữ liệu trả về của phương thức
    // Function<Claims, T> claimResolver : Interface này đại diện cho một hàm nhận vào một đối tượng Claims (chứa các claims của JWT) và trả về một giá trị kiểu T
    // Function<T, R> một hàm (function) nhận vào một tham số kiểu T và trả về một kết quả kiểu R
    // R apply(T t): nhận vào một tham số t kiểu T và trả về một kết quả kiểu R.
    private <T> T extractClaim(String token,  TokenType type,Function<Claims, T> claimResolver) {
        final Claims claims = extraAllClaim(token,type);
        return claimResolver.apply(claims);
    }

    // Phân tích JWT và trả về đối tượng Claims chứa tất cả các claims.
    // parseClaimsJws(token):  phân tích JWT token và trả về một đối tượng Jws<Claims>
    // getBody(): trên đối tượng Jws<Claims> để lấy phần payload (nội dung) của JWT
    private Claims extraAllClaim(String token, TokenType type) {
        return Jwts.parserBuilder().setSigningKey(getKey(type)).build().parseClaimsJws(token).getBody();
    }

    //  kiểm tra xem token đã hết hạn
    private boolean isTokenExpired(String token, TokenType type ) {
        return extractExpiration(token, type).before(new Date());
    }

    // tra ve ngày hết hạn của token
    private Date extractExpiration(String token,TokenType type) {
        return extractClaim(token,type ,Claims::getExpiration);
    }
//    public String generateToken(String usename)
//    {
//        JWSHeader header = new JWSHeader(JWSAlgorithm.ES512);
//        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
//                .subject(usename)
//                .issuer("nhatro.com")
//                .issueTime(new Date())
//                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()))
//                .build();
//        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
//
//        JWSObject jwsObject = new JWSObject(header, payload);
//    // ký token
//        try {
//            jwsObject.sign(new MACSigner(signerKey.getBytes()));
//            return jwsObject.serialize();
//        } catch (JOSEException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
