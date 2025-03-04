package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.DTO.request.ResetPasswordDTO;
import com.example.nhatrobackend.DTO.request.SignInRequest;
import com.example.nhatrobackend.DTO.respone.TokenResponse;
//import com.example.nhatrobackend.Entity.Account;
import com.example.nhatrobackend.Entity.Field.UserType;
import com.example.nhatrobackend.Entity.Token;
import com.example.nhatrobackend.Entity.User;
//import com.example.nhatrobackend.Mapper.AccountMapper;
import com.example.nhatrobackend.Mapper.UserMapper;
//import com.example.nhatrobackend.Responsitory.AccountRepository;
import com.example.nhatrobackend.Responsitory.UserRepository;
import com.example.nhatrobackend.util.TokenType;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import static com.example.nhatrobackend.Entity.Field.UserStatus.ACTIVE;
import static com.example.nhatrobackend.Entity.Field.LandlordStatus.NOT_REGISTERED;
import static com.example.nhatrobackend.util.TokenType.*;
import static org.springframework.http.HttpHeaders.REFERER;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    // dính lỗi khi sửa security xóa bảng account
//    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
//    private final AccountMapper accountMapper;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final  TokenService tokenService;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;


    public TokenResponse authenticate(SignInRequest signInRequest) {
        log.info("---------- authenticate ----------");

        var user = userService.findByPhoneNumber(signInRequest.getPhoneNumber());

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("User not active");
        }
//
//        List<String> roles = userService.getAllRolesByUserId(user.getId());
//        List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getPhoneNumber(), signInRequest.getPassword()));

        // create new access token
        String accessToken = jwtService.generateToken(user);
        log.info("---------- generateToken authenticate ----------");
        String refreshToken = jwtService.generateRefreshToken(user);
        log.info("---------- generateRefreshToken authenticate ----------");
        tokenService.save(Token.builder().phoneNumber(user.getPhoneNumber()).accessToken(accessToken).refreshToken(refreshToken).build());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .userType(user.getType())
                .build();
    }

    public TokenResponse refreshToken(HttpServletRequest request) {
        log.info("---------- refreshToken ----------");
        final String refreshToken = request.getHeader(REFERER);

        if(StringUtils.isBlank(refreshToken)){
            throw new IllegalArgumentException("Token must be not blank");
        }
        final String userName = jwtService.extractUsername(refreshToken,REFRESH_TOKEN);
        var user = userService.findByPhoneNumber(userName);
        if(!jwtService.isValid(refreshToken,REFRESH_TOKEN,user)){
            throw new IllegalArgumentException("Not allow access with this token");
        }

        // create new access token
        String accessToken = jwtService.generateToken(user);

        log.info("---------- refreshToken create new access token ----------");
        // save token to db
        Token token = tokenService.getByPhoneNumber(user.getPhoneNumber());
        token.setAccessToken(accessToken);
        tokenService.save(token);

//        tokenService.save(Token.builder().phoneNumber(user.getPhoneNumber()).accessToken(accessToken).refreshToken(refreshToken).build());



        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .build();
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

    public String logout(HttpServletRequest request) {
        log.info("---------- logout ----------");
        final String token = request.getHeader(REFERER);
        if(StringUtils.isBlank(token)){
            throw new IllegalArgumentException("Token must be not blank");
        }
        final String phoneNumber = jwtService.extractUsername(token,ACCESS_TOKEN);
        tokenService.delete(phoneNumber);
        return "Removed!";
    }

    public String forgotPassword(String email) {
        log.info("---------- forgotPassword ----------");

        // Check email exist or not
        User user = userService.getUserByEmail(email);

        // generate reset token
        String resetToken = jwtService.generateResetToken(user);

        //save reset token to db
        tokenService.save(Token.builder().phoneNumber(user.getPhoneNumber()).resetToken(resetToken).build());
        // TODO send email to user
        String confirmLink = String.format("curl --location 'http://localhost:80/auth/reset-password' \\\n" +
                "--header 'accept: */*' \\\n" +
                "--header 'Content-Type: application/json' \\\n" +
                "--data '%s'", resetToken);
        log.info("--> confirmLink: {}", confirmLink);
        return resetToken;
    }

    public String resetPassword(String secretKey) {
        log.info("---------- resetPassword ----------");
        //validate token
        var user = validateToken(secretKey);

        // check token by username
//        tokenService.
        return "Valid Reset Password";
    }

    public String changePassword(ResetPasswordDTO request) {
        log.info("---------- changePassword ----------");

        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new IllegalArgumentException("Passwords do not match");
        }

        var user = validateToken(request.getSecretKey());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.saveUser(user);
        return "Changed";
    }
    private User validateToken(String token){
        // validate token
        var userName = jwtService.extractUsername(token,RESET_TOKEN);

        // validate user is active or not
        var user = userService.findByPhoneNumber(userName);
        if(!user.isEnabled()){
            throw new IllegalArgumentException("User not active");
        }
        return user;
    }

    public void updatePassword(String userUuid, ChangePasswordRequest request) {
        User user = userService.findByUserUuid(userUuid);

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác");
        }

        // Kiểm tra xác nhận mật khẩu mới
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận không khớp");
        }

        // Cập nhật mật khẩu mới sau khi mã hóa
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Lưu thay đổi vào cơ sở dữ liệu
        userRepository.save(user);
    }


    //    public AuthenticationResponse authenticate(AuthenticationRequest request){
//        Optional<Account> optionalAccount = accountRepository.findByPhoneNumber(request.getPhoneNumber());
//        if(optionalAccount.isPresent()){
//            Account account = optionalAccount.get();
//            if(passwordEncoder.matches(request.getPassword(),account.getPassword())){
////                var token = generateToken(request.getPhoneNumber());
//                var token = generateToken(account);
//                return AuthenticationResponse.builder()
//                        .accessToken(token)
//                        .authenticated(true)
//                        .fullName(account.getUser().getFullName())
//                        .userType(account.getUserType())
//                        .build();
//            } else{
//                throw new IllegalArgumentException("Mật khẩu không chính xác");
//            }
//        } else{
//            throw new EntityNotFoundException("Số điện thoại chưa đăng ký " + request.getPhoneNumber());
//
//        }
//    }
//    private String generateToken(Account account){
//        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
//        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
//                .subject (account.getPhoneNumber())
//                .issuer("example.com")
//                .issueTime(new Date())
//                .expirationTime(new Date(
//                Instant.now().plus( 30, ChronoUnit.DAYS).toEpochMilli()
//                ))
//                .claim( "scope", buildScope(account))
//                .claim( "sub", userUuid(account))
//                .build();
//        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
//        JWSObject jwsObject = new JWSObject(header,payload);
//        try {
//            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
//            return jwsObject.serialize();
//        } catch (JOSEException e) {
//            throw new RuntimeException(e);
//        }
//
//    }

//    // Đăng ký tài khoản và gửi OTP
//    public void register(RegisterRequestDTO dto) {
//        // Kiểm tra số điện thoại đã tồn tại chưa
//        if (accountRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
//            throw new IllegalArgumentException("Số điện thoại đã được đăng ký");
//        }
//
//        // Gửi OTP
//        otpService.sendOtp(dto.getPhoneNumber());
//    }

    // dính lỗi khi sửa security xóa bảng account
//
//    public void verifyOtpAndCreateAccount(OtpVerificationDTO dto){
//        if(!otpService.verifyOtp(dto.getPhoneNumber(), dto.getOtp())){
//            throw new IllegalArgumentException("OTP không hợp lệ");
//        }
//
//        String encodedPassword = passwordEncoder.encode(dto.getPassword());
//        User user = userMapper.toEntity(dto);
//        user.setIsLandlordActivated(NOT_REGISTERED);
//        user.setCreatedAt(LocalDateTime.now()); // Thêm trường không có trong DTO
//        User savedUser = userRepository.save(user);
//
//        Account account = accountMapper.toEntity(dto);
//        account.setPassword(encodedPassword);
////        account.setUser(savedUser);
//        account.setUserType(UserType.TENANT);
//        account.setStatus(ACTIVE);
//        accountRepository.save(account);
//    }
//
//    private String buildScope (Account account){
//        StringJoiner stringJoiner = new StringJoiner (" ");
//        if (account.getUserType() !=null){
//            stringJoiner.add(account.getUserType().name());
//        }
//        return stringJoiner.toString();
//    }
//
//    // // dính lỗi khi sửa security xóa bảng account
//    private String userUuid (Account account) {
//        // Lấy userUuid từ User liên quan đến Account
////        User user = account.getUser();
////        if (user != null) {
////            return user.getUserUuid();  // Giả sử User có thuộc tính userUuid
////        } else {
////            return null;  // Trường hợp không tìm thấy User
////        }
//        return null;
//    }
//
//    public void changePassword(String userUuid, ChangePasswordRequest request) {
//        User user = userService.findByUserUuid(userUuid);
//
//        // Lấy tài khoản dựa trên userUuid
//        Account account = accountRepository.findByUser_UserId(user.getUserId())
//                .orElseThrow(() -> new EntityNotFoundException("Tài khoản không tồn tại"));
//
//        // Kiểm tra mật khẩu hiện tại
//        if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
//            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác");
//        }
//
//        // Kiểm tra xác nhận mật khẩu mới
//        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
//            throw new IllegalArgumentException("Mật khẩu mới và xác nhận không khớp");
//        }
//
//        // Cập nhật mật khẩu mới sau khi mã hóa
//        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
//
//        // Lưu thay đổi vào cơ sở dữ liệu
//        accountRepository.save(account);
//    }

}
