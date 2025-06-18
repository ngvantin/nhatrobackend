package com.example.nhatrobackend.Entity.Field;

public enum DepositStatus {
    PENDING, // Đang chờ thanh toán
    PAID, // Đã thanh toán mới đặt cọc
    CONFIRMED, // Đã xác nhận từ 1 hoặc cả hai bên
    CONFIRMEDPENDING, //CHỜ thanh toán thuận lợi
    SUCCESS, //admin thanh toán giao dịch thuận lợi
    CANCELLED, // Đã hủy từ 1 hoặc cả hai bên
    REFUNDED, // CHỜ thanh toán hoàn tiền cho người cọc
    REFUNDEDSUCCESS, // admin thanh toán Đã hoàn tiền cho người cọc
    COMMISSION, // CHỜ thanh toán cho Chủ trọ thắng khiếu nại
    COMMISSIONSUCCESS, // admin thanh toán Chủ trọ thắng khiếu nại

    EXPIRED // Hết hạn giữ chỗ
} 