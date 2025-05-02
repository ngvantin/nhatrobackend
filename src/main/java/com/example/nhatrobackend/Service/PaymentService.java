package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.request.PaymentRequest;
import com.example.nhatrobackend.DTO.response.VNPayResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    public VNPayResponse createVnPayPayment(PaymentRequest paymentRequest, HttpServletRequest request,Integer currentUserId);
    public String processPaymentSuccess(String transactionCode, Long amount, String orderInfo);
    public String processPaymentFailed(String transactionCode, String responseCode, String orderInfo);

    String processVnPayCallback(HttpServletRequest request);
}
