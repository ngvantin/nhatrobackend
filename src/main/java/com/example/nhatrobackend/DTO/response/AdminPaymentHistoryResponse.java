package com.example.nhatrobackend.DTO.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminPaymentHistoryResponse {
    private Long paymentId;
    private String orderInfo;
    private Long paymentAmount;
    private String transactionCode;
    private LocalDateTime paymentTime;
    private String responseCode;
    
    // User information
    private Integer userId;
    private String fullName;
    private String email;
    private String phoneNumber;
} 