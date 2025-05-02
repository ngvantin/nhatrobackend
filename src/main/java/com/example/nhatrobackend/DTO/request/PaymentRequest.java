package com.example.nhatrobackend.DTO.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private Integer amount;
    private String bankCode;
}
