package com.example.nhatrobackend.Entity.Field;

public enum DepositStatus {
    PENDING, // Đang chờ thanh toán
    PAID, // Đã thanh toán mới đặt cọc1
    CONFIRMED, // Đã xác nhận từ 1 hoặc cả hai bên1
    CONFIRMEDPENDING, //CHỜ thanh toán thuận lợi1
    SUCCESS, //admin thanh toán giao dịch thuận lợi1
    CANCELLED, // Đã hủy từ 1 hoặc cả hai bên1
    REFUNDED, // CHỜ thanh toán hoàn tiền cho người cọc1
    REFUNDEDSUCCESS, // admin thanh toán Đã hoàn tiền cho người cọc1
    COMMISSION, // CHỜ thanh toán cho Chủ trọ thắng khiếu nại1
    COMMISSIONSUCCESS, // admin thanh toán Chủ trọ thắng khiếu nại1

    EXPIRED // Hết hạn giữ chỗ
} 