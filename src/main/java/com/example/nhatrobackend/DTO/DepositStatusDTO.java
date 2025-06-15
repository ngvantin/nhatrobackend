package com.example.nhatrobackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositStatusDTO {
    private int depositId;
    private Double amount;
    private String paymentMethod;
    private String transactionId;
} 