package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.DepositStatus;
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
    private String postUuid;
    private DepositStatus status;
} 