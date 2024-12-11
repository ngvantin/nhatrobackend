package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.Service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

//    @PostMapping("/login")
//    public ResponseEntity<ResponseWrapper<AuthenticationResponse>> login(@RequestBody AuthenticationRequest request){
//        var result = authenticationService.authenticate(request);
//                return ResponseEntity.ok(ResponseWrapper.<AuthenticationResponse>builder()
//                .status("success")
//                .data(result)
//                .message("Đăng nhập thành công")
//                .build());
//    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<AuthenticationResponse>> login(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response) {
        // Gọi service để xử lý xác thực
        var result = authenticationService.authenticate(request);

        // Tạo cookie để lưu JWT token
        Cookie cookie = new Cookie("jwtToken", result.getAccessToken());
        cookie.setHttpOnly(true); // Cookie chỉ được gửi qua HTTP, không thể truy cập bằng JavaScript
        cookie.setSecure(true);  // Chỉ gửi qua HTTPS
        cookie.setPath("/");     // Cookie được gửi kèm với mọi endpoint
        cookie.setMaxAge(24 * 60 * 60); // Thời gian sống: 1 ngày

        // Thêm cookie vào response
        response.addCookie(cookie);

        // Trả về response
        return ResponseEntity.ok(ResponseWrapper.<AuthenticationResponse>builder()
                .status("success")
                .data(result)
                .message("Đăng nhập thành công")
                .build());
    }


    @PostMapping("/introspect")
    public ResponseEntity<ResponseWrapper<IntrospectResponse>> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        System.out.printf("run here");

        var result = authenticationService.introspect(request);
        System.out.printf(String.valueOf(result));
        return ResponseEntity.ok(ResponseWrapper.<IntrospectResponse>builder()
                .data(result)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<RegisterRequestDTO>> register(@Valid @RequestBody RegisterRequestDTO dto){
        authenticationService.register(dto);
        return ResponseEntity.ok(ResponseWrapper.<RegisterRequestDTO>builder()
                .status("success")
                .data(dto)
                .message("OTP đã được gửi qua số điện thoại của bạn")
                .build());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseWrapper> verifyOtp(@Valid @RequestBody OtpVerificationDTO dto){
        authenticationService.verifyOtpAndCreateAccount(dto);
        return ResponseEntity.ok(ResponseWrapper.builder()
                .status("success")
                .message("Đăng ký thành công")
                .build());
    }
}
