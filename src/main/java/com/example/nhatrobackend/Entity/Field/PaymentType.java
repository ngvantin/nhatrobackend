package com.example.nhatrobackend.Entity.Field;

public enum PaymentType {
    VNPAY("VNPAY"), // Thanh toán qua VNPAY
    REFUND("REFUND"), // Hoàn tiền cho người đặt cọc
    COMMISSION("COMMISSION"); // Hoa hồng cho chủ trọ

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentType fromValue(String value) {
        for (PaymentType type : PaymentType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PaymentType value: " + value);
    }
}