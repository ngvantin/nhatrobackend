package com.example.nhatrobackend.Entity.Field;

public enum EventType {
    POST_APPROVED("Duyệt bài đăng"),
    POST_REJECTED("Từ chối bài đăng"),
    POST_REPORTED("Báo cáo bài đăng"),
    LANDLORD_APPROVED("Duyệt tài khoản chủ trọ"),
    ROOM_INTERESTED("Quan tâm phòng trọ"),
    PRICE_UPDATED("Cập nhật giá phòng"),
    ROOM_STATUS_CHANGED("Thay đổi trạng thái phòng"),
    USER_REGISTERED("Đăng ký tài khoản mới"),
    SYSTEM_NOTIFICATION("Thông báo hệ thống"),
    POST_LOCKED("Khóa bài đăng"),
    NEW_POST_FROM_FOLLOWING("Bài viết mới"),
    LANDLORD_REJECTED("Từ chối tài khoản chủ trọ"),
    MATCHING_POST("Bài đăng được đề xuất");

    private final String description;

    EventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}