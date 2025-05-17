//package com.example.nhatrobackend.DTO.annotation;
//
//
//import com.example.nhatrobackend.Entity.Field.EventType;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
//import static com.example.nhatrobackend.Entity.Field.EventType.SYSTEM_NOTIFICATION;
//
///**
// * Annotation để định nghĩa template cho các thông báo liên quan đến nhà trọ.
// * Sử dụng để tạo các mẫu thông báo chuẩn cho:
// * - Thông báo duyệt bài
// * - Thông báo từ chối bài
// * - Thông báo có người quan tâm
// * - Thông báo cập nhật giá/trạng thái phòng
// */
//@Target(ElementType.TYPE)
//@Retention(RetentionPolicy.RUNTIME)
//public @interface RentalNotificationTemplate {
//    String value() default "";
//
//    /**
//     * ID của template
//     */
//    String templateId();
//
//    /**
//     * Tiêu đề mẫu của thông báo
//     */
//    String title();
//
//    /**
//     * Nội dung mẫu của thông báo
//     * Hỗ trợ các placeholder:
//     * {postTitle} - Tiêu đề bài đăng
//     * {roomPrice} - Giá phòng
//     * {address} - Địa chỉ phòng
//     * {landlordName} - Tên chủ trọ
//     * {userName} - Tên người dùng
//     */
//    String content();
//
//    /**
//     * Loại thông báo
//     */
//    EventType type() default SYSTEM_NOTIFICATION;
//
//    /**
//     * URL chuyển hướng khi click vào thông báo
//     */
//    String redirectUrl() default "";
//}