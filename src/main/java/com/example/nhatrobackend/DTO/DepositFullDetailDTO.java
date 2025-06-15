package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.DepositStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositFullDetailDTO {
    private int depositId;
    private DepositStatus status;
    private Boolean landlordConfirmed;
    private Boolean tenantConfirmed;
    
    private String tenantComplaintReason;
    private String tenantComplaintVideoUrl;
    private String landlordComplaintReason;
    private String landlordComplaintVideoUrl;
    
    private List<String> tenantComplaintImages;
    private List<String> landlordComplaintImages;
    
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