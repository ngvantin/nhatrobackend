package com.example.nhatrobackend.DTO.request;

import lombok.Data;

@Data
public class DepositRequest {
    private Integer postId; // ID của bài đăng
    private Double amount; // Số tiền đặt cọc
    private String bankCode; // Mã ngân hàng (nếu có)
} 