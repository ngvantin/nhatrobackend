package com.example.nhatrobackend.Entity.Field;

public enum DepositStatus {
    PENDING, // Đang chờ thanh toán
    PAID, // Đã thanh toán mới đặt cọc
    CONFIRMED, // Đã xác nhận từ 1 hoặc cả hai bên
    CANCELLED, // Đã hủy từ 1 hoặc cả hai bên
    REFUNDED, // Đã hoàn tiền
    EXPIRED // Hết hạn giữ chỗ
} 