package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.DepositStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositResponseDTO {
    private Integer depositId;
    private String postUuid;
    private String postTitle;
    private Double amount;
    private DepositStatus status;
    private LocalDateTime createdAt;
    private String userFullName;
    private String userUuid;
} 