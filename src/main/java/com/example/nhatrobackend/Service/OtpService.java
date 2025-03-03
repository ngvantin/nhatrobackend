package com.example.nhatrobackend.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value; // Đọc giá trị từ application.properties
import org.springframework.stereotype.Service; // Đánh dấu class là Service
import org.springframework.web.client.RestTemplate; // Gửi HTTP requests

import java.util.Map; // Xử lý các cấu trúc dữ liệu dạng key-value
import java.util.List; // Xử lý danh sách các SMS gửi trong payload
import java.util.Random; // Sinh mã OTP ngẫu nhiên
import java.util.concurrent.ConcurrentHashMap; // Bộ nhớ cache cho OTP

// Các import liên quan đến JWT
import com.fasterxml.jackson.databind.ObjectMapper; // Xử lý JSON để tạo JWT
import org.apache.commons.codec.digest.HmacUtils; // Sinh chữ ký HMAC-SHA256
import java.util.Base64; // Mã hóa JWT header và payload


@Service
public class OtpService {
    private final Map<String, String> otpCache = new ConcurrentHashMap<>();

    public void sendOtp(String phoneNumber) {
        String otp = generateOtp();
        otpCache.put(phoneNumber, otp);
        System.out.println("OTP gửi qua " + phoneNumber + ": " + otp);
    }

    // Xác thực OTP
    public boolean verifyOtp(String phoneNumber, String otp) {
        String cachedOtp = otpCache.get(phoneNumber);
        return cachedOtp != null && cachedOtp.equals(otp);
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(899999) + 100000);
    }
}
//}@RequiredArgsConstructor
////@Service
////public class OtpService {
////    private final Map<String, String> otpCache = new ConcurrentHashMap<>();
////    private final RestTemplate restTemplate;
////
////    // API key SID và secret từ Stringee
////    @Value("${stringee.apiKeySid}")
////    private String apiKeySid;
////
////    @Value("${stringee.apiKeySecret}")
////    private String apiKeySecret;
////
////    public void sendOtp(String phoneNumber) {
////        System.out.println("Sending OTP to: " + phoneNumber);
////        System.out.println("API Key SID: " + apiKeySid);
////        System.out.println("API Key Secret: " + apiKeySecret);
////
////        phoneNumber = formatPhoneNumber(phoneNumber);
////        String otp = generateOtp();
////        otpCache.put(phoneNumber, otp);
////        System.out.println("Số điện thoại đã được định dạng: " + phoneNumber);
////
////        String jwt = generateJwtToken();
////        String apiUrl = "https://api.stringee.com/v1/sms";
////
////        // Cấu hình body để gửi SMS
////        Map<String, Object> smsPayload = Map.of(
////                "sms", List.of(
////                        Map.of(
////                                "from", "TroTot", // Thay bằng BrandName đã đăng ký với Stringee
////                                "to", phoneNumber,
////                                "text", "Mã OTP của bạn là: " + otp
////                        )
////                )
////        );
////
////        HttpHeaders headers = new HttpHeaders();
////        headers.setContentType(MediaType.APPLICATION_JSON);
////        headers.set("X-STRINGEE-AUTH", jwt);
////
////        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(smsPayload, headers);
////
////        try {
////            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
////
////            if (!response.getStatusCode().is2xxSuccessful()) {
////                System.err.println("Response Status: " + response.getStatusCode());
////                System.err.println("Response Body: " + response.getBody());
////                throw new RuntimeException("Gửi OTP thất bại. Mã trạng thái: " + response.getStatusCode() +
////                        ", Nội dung phản hồi: " + response.getBody());
////            }
////            System.out.println("OTP sent successfully.");
////        } catch (HttpClientErrorException | HttpServerErrorException e) {
////            System.err.println("Error Status Code: " + e.getStatusCode());
////            System.err.println("Error Response Body: " + e.getResponseBodyAsString());
////            throw new RuntimeException("Lỗi khi gọi Stringee API: " + e.getResponseBodyAsString(), e);
////        } catch (Exception e) {
////            System.err.println("Unexpected error: " + e.getMessage());
////            throw new RuntimeException("Lỗi không xác định khi gửi OTP", e);
////        }
////
////
////    }
////
////    public boolean verifyOtp(String phoneNumber, String otp) {
////        String cachedOtp = otpCache.get(phoneNumber);
////        return cachedOtp != null && cachedOtp.equals(otp);
////    }
////
////    private String generateOtp() {
////        return String.valueOf(new Random().nextInt(899999) + 100000);
////    }
////
////    private String generateJwtToken() {
////        Map<String, Object> header = Map.of(
////                "typ", "JWT",
////                "alg", "HS256",
////                "cty", "stringee-api;v=1"
////        );
////
////        long currentTime = System.currentTimeMillis() / 1000L;
////        Map<String, Object> payload = Map.of(
////                "jti", apiKeySid + "_" + currentTime,
////                "iss", apiKeySid,
////                "exp", currentTime + 3600,
////                "rest_api", true
////        );
////
////        try {
////            String headerEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(new ObjectMapper().writeValueAsBytes(header));
////            String payloadEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(new ObjectMapper().writeValueAsBytes(payload));
////
////            String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(
////                    HmacUtils.hmacSha256(apiKeySecret, headerEncoded + "." + payloadEncoded)
////            );
////
////            return headerEncoded + "." + payloadEncoded + "." + signature;
////        } catch (Exception e) {
////            System.err.println("Error generating JWT token: " + e.getMessage());
////            throw new RuntimeException("Không thể tạo JWT token", e);
////        }
////    }
////
////
////    private String formatPhoneNumber(String phoneNumber) {
////        if (phoneNumber.startsWith("0")) {
////            // Thay 0 bằng +84 nếu là số điện thoại Việt Nam
////            return "+84" + phoneNumber.substring(1);
////        } else if (!phoneNumber.startsWith("+")) {
////            throw new IllegalArgumentException("Số điện thoại không hợp lệ: " + phoneNumber);
////        }
////        return phoneNumber;
////    }
////
////}
//

