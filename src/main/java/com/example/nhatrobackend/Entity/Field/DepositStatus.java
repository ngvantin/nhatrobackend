package com.example.nhatrobackend.Entity.Field;

public enum DepositStatus {
    PENDING, // Đang chờ thanh toán
    PAID, // Đã thanh toán
    CONFIRMED, // Đã xác nhận từ cả hai bên
    CANCELLED, // Đã hủy
    REFUNDED, // Đã hoàn tiền
    EXPIRED // Hết hạn giữ chỗ
} 