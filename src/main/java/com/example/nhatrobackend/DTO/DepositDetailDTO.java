package com.example.nhatrobackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositDetailDTO {
    private Integer depositId;
    private LocalDateTime createdAt;
    
    // Thông tin người đặt cọc
    private Integer depositorId;
    private String depositorFullName;
    private String depositorEmail;
    private String depositorPhoneNumber;
    
    // Thông tin người đăng bài (chủ trọ)
    private Integer landlordId;
    private String landlordFullName;
    private String landlordEmail;
    private String landlordPhoneNumber;
} 