package com.example.nhatrobackend.Service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private final Map<String,String> otpCache = new ConcurrentHashMap<>();

    public void sendOtp(String phoneNumber){
        String otp = generateOtp();
        otpCache.put(phoneNumber, otp);
        System.out.println("OTP gửi qua " + phoneNumber + ": " + otp);
    }

    // Xác thực OTP
    public boolean verifyOtp(String phoneNumber, String otp) {
        String cachedOtp = otpCache.get(phoneNumber);
        return cachedOtp != null && cachedOtp.equals(otp);
    }

    private String generateOtp(){
        return String.valueOf(new Random().nextInt(899999)+100000);
    }
}
