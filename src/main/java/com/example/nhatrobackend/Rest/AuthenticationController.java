package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.DTO.request.*;
import com.example.nhatrobackend.DTO.request.RegisterRequestDTO;
import com.example.nhatrobackend.DTO.response.TokenResponse;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.AuthenticationService;
import com.example.nhatrobackend.Service.NotificationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final AuthenticationFacade authenticationFacade;
    private final NotificationService notificationService;
    // khi sửa security xóa bảng account
    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<TokenResponse>> login(@RequestBody SignInRequest request) {
//        return new ResponseEntity<>(authenticationService.authenticate(request), OK);
        TokenResponse result = authenticationService.authenticate(request);
        long unreadNotificationCount = notificationService.getUnreadNotificationCountForUser(result.getUserId());
        result.setUnreadNotificationCount(unreadNotificationCount);
            return ResponseEntity.ok(ResponseWrapper.<TokenResponse>builder()
            .status("success")
            .data(result)
            .message("Đăng nhập thành công")
            .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseWrapper<TokenResponse>> refresh(@RequestBody RefreshTokenRequest request){
        var result =  authenticationService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ResponseWrapper.<TokenResponse>builder()
                .status("success")
                .data(result)
                .message("Refresh thành công")
                .build());
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<ResponseWrapper<TokenResponse>> refresh(HttpServletRequest request){
//        var result =  authenticationService.refreshToken(request);
//        return ResponseEntity.ok(ResponseWrapper.<TokenResponse>builder()
//                .status("success")
//                .data(result)
//                .message("Refresh thành công")
//                .build());
//    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseWrapper<String>> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization"); // Lấy token từ header Authorization
        var result = authenticationService.logout(token);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .data(result)
                .message("Logout thành công")
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseWrapper<String>> forgotPassword(@RequestBody ForgotPasswordRequest request){
        var result =  authenticationService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .data(result)
                .message("Forgot password thành công")
                .build());
    }

    @GetMapping("/reset-password")
    public ResponseEntity<ResponseWrapper<String>> resetPassword(@RequestParam String secretKey) {
        var result =  authenticationService.resetPassword(secretKey);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .data(result)
                .message("Reset password thành công")
                .build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseWrapper<String>> changePassword(@RequestBody @Valid ResetPasswordDTO request) {
        var result =  authenticationService.changePassword(request);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .data(result)
                .message("Change password thành công")
                .build());
    }

    @PostMapping("/update-password")
    public ResponseEntity<ResponseWrapper<String>> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            HttpServletRequest httpServletRequest) {

        // Lấy userUuid từ JWT token trong cookie thông qua authenticationFacade
//        String userUuid = authenticationFacade.getCurrentUserUuid(httpServletRequest);
        String userUuid = authenticationFacade.getCurrentUserUuid();


        // Gọi service để xử lý thay đổi mật khẩu
        authenticationService.updatePassword(userUuid, request);

        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .message("Đổi mật khẩu thành công")
                .data("Mật khẩu đã được thay đổi")
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
    public ResponseEntity<ResponseWrapper<String>> register(@Valid @RequestBody RegisterRequestDTO request) {
        var result = authenticationService.register(request);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .data(result)
                .message("Verification email has been sent to your email address")
                .build());
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ResponseWrapper<String>> verifyEmail(@RequestParam String secretKey) {
        var result = authenticationService.verifyEmail(secretKey);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .data(result)
                .message("Email verified successfully")
                .build());
    }

//    @PostMapping("/register")
//    public ResponseEntity<ResponseWrapper<RegisterRequestDTO>> register(@Valid @RequestBody RegisterRequestDTO dto){
//        authenticationService.register(dto);
//        return ResponseEntity.ok(ResponseWrapper.<RegisterRequestDTO>builder()
//                .status("success")
//                .data(dto)
//                .message("OTP đã được gửi qua số điện thoại của bạn")
//                .build());
//    }
//
//    @PostMapping("/verify-otp")
//    public ResponseEntity<ResponseWrapper> verifyOtp(@Valid @RequestBody OtpVerificationDTO dto){
//        authenticationService.verifyOtpAndCreateAccount(dto);
//        return ResponseEntity.ok(ResponseWrapper.builder()
//                .status("success")
//                .message("Đăng ký thành công")
//                .build());
//    }
//
//    @PostMapping("/change-password")
//    public ResponseEntity<ResponseWrapper<String>> changePassword(
//            @RequestBody @Valid ChangePasswordRequest request,
//            HttpServletRequest httpServletRequest) {
//
//        // Lấy userUuid từ JWT token trong cookie thông qua authenticationFacade
//        String userUuid = authenticationFacade.getCurrentUserUuid(httpServletRequest);
//
//        // Gọi service để xử lý thay đổi mật khẩu
//        authenticationService.changePassword(userUuid, request);
//
//        return ResponseEntity.ok(ResponseWrapper.<String>builder()
//                .status("success")
//                .message("Đổi mật khẩu thành công")
//                .data("Mật khẩu đã được thay đổi")
//                .build());
//    }

//    @PostMapping("/refresh")
//    public ResponseEntity<TokenResponse> refresh(HttpServletRequest request) {
//        return new ResponseEntity<>(authenticationService.refreshToken(request), OK);
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(HttpServletRequest request) {
//        return new ResponseEntity<>(authenticationService.logout(request), OK);
//    }

    //    @PostMapping("/logout")
//    public ResponseEntity<ResponseWrapper<String>> logout(HttpServletRequest request) {
//        var result =  authenticationService.logout(request);
//        return ResponseEntity.ok(ResponseWrapper.<String>builder()
//                .status("success")
//                .data(result)
//                .message("Logout thành công")
//                .build());
//    }

    //    @PostMapping("/login")
//    public ResponseEntity<ResponseWrapper<AuthenticationResponse>> login(@RequestBody AuthenticationRequest request){
//        var result = authenticationService.authenticate(request);
//                return ResponseEntity.ok(ResponseWrapper.<AuthenticationResponse>builder()
//                .status("success")
//                .data(result)
//                .message("Đăng nhập thành công")
//                .build());
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<ResponseWrapper<AuthenticationResponse>> login(
//            @RequestBody AuthenticationRequest request,
//            HttpServletResponse response) {
//        // Gọi service để xử lý xác thực
//        var result = authenticationService.authenticate(request);
//
//        // Tạo cookie để lưu JWT token
//        Cookie cookie = new Cookie("jwtToken", result.getAccessToken());
//        cookie.setHttpOnly(true); // Cookie chỉ được gửi qua HTTP, không thể truy cập bằng JavaScript
//        cookie.setSecure(true);  // Chỉ gửi qua HTTPS
//        cookie.setPath("/");     // Cookie được gửi kèm với mọi endpoint
//        cookie.setMaxAge(24 * 60 * 60); // Thời gian sống: 1 ngày
//
//        // Thêm cookie vào response
//        response.addCookie(cookie);
//
//        // Trả về response
//        return ResponseEntity.ok(ResponseWrapper.<AuthenticationResponse>builder()
//                .status("success")
//                .data(result)
//                .message("Đăng nhập thành công")
//                .build());
//    }

}
