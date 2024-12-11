package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.Entity.Account;
import com.example.nhatrobackend.Entity.Field.Role;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Mapper.AccountMapper;
import com.example.nhatrobackend.Mapper.UserMapper;
import com.example.nhatrobackend.Responsitory.AccountRepository;
import com.example.nhatrobackend.Responsitory.UserRepository;
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
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.StringJoiner;

import static com.example.nhatrobackend.Entity.Field.AccountStatus.ACTIVE;
import static com.example.nhatrobackend.Entity.Field.LandlordStatus.NOT_REGISTERED;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AccountMapper accountMapper;

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
//                var token = generateToken(request.getPhoneNumber());
                var token = generateToken(account);
                return AuthenticationResponse.builder()
                        .accessToken(token)
                        .authenticated(true)
                        .fullName(account.getUser().getFullName())
                        .role(account.getRole())
                        .build();
            } else{
                throw new IllegalArgumentException("Mật khẩu không chính xác");
            }
        } else{
            throw new EntityNotFoundException("Số điện thoại chưa đăng ký " + request.getPhoneNumber());

        }
    }
    private String generateToken(Account account){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject (account.getPhoneNumber())
                .issuer("example.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                Instant.now().plus( 30, ChronoUnit.DAYS).toEpochMilli()
                ))
                .claim( "scope", buildScope(account))
                .claim( "sub", userUuid(account))
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
        System.out.printf("run here2");
        var token = request.getToken();
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();
    }

    // Đăng ký tài khoản và gửi OTP
    public void register(RegisterRequestDTO dto) {
        // Kiểm tra số điện thoại đã tồn tại chưa
        if (accountRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("Số điện thoại đã được đăng ký");
        }

        // Gửi OTP
        otpService.sendOtp(dto.getPhoneNumber());
    }

    public void verifyOtpAndCreateAccount(OtpVerificationDTO dto){
        if(!otpService.verifyOtp(dto.getPhoneNumber(), dto.getOtp())){
            throw new IllegalArgumentException("OTP không hợp lệ");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = userMapper.toEntity(dto);
        user.setIsLandlordActivated(NOT_REGISTERED);
        user.setCreatedAt(LocalDateTime.now()); // Thêm trường không có trong DTO
        User savedUser = userRepository.save(user);

        Account account = accountMapper.toEntity(dto);
        account.setPassword(encodedPassword);
        account.setUser(savedUser);
        account.setRole(Role.TENANT);
        account.setStatus(ACTIVE);
        accountRepository.save(account);
    }

    private String buildScope (Account account){
        StringJoiner stringJoiner = new StringJoiner (" ");
        if (account.getRole() !=null){
            stringJoiner.add(account.getRole().name());
        }
        return stringJoiner.toString();
    }
    private String userUuid (Account account) {
        // Lấy userUuid từ User liên quan đến Account
        User user = account.getUser();
        if (user != null) {
            return user.getUserUuid();  // Giả sử User có thuộc tính userUuid
        } else {
            return null;  // Trường hợp không tìm thấy User
        }
    }

}
