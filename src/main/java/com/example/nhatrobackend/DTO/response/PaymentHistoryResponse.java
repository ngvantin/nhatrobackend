package com.example.nhatrobackend.DTO.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentHistoryResponse {
    private String orderInfo;
    private Long paymentAmount;
    private String transactionCode;
    private LocalDateTime paymentTime;
}
