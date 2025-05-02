package com.example.nhatrobackend.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VNPayResponse {
    public String code;
    public String message;
    public String paymentUrl;
}
