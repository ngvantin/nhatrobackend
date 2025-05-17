package com.example.nhatrobackend.DTO.response;

import lombok.*;

/**
 * DTO chuẩn hóa response lỗi cho các API thông báo
 */
@Data
@Builder
public class ErrorResponse {
    private String message;
    private String errorCode;
}