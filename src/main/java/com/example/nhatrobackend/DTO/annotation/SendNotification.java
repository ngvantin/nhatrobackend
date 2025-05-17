//package com.example.nhatrobackend.DTO.annotation;
//
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
///**
// * Annotation để đánh dấu các phương thức cần gửi thông báo trong hệ thống nhà trọ.
// * Sử dụng cho các trường hợp như:
// * - Duyệt/từ chối bài đăng
// * - Duyệt tài khoản chủ trọ
// * - Có người quan tâm phòng trọ
// * - Cập nhật trạng thái phòng
// */
//
//import com.example.nhatrobackend.Entity.Field.EventType;
//import com.example.nhatrobackend.DTO.NotificationEvent.Priority;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
//@Target(ElementType.METHOD)
//@Retention(RetentionPolicy.RUNTIME)
//public @interface SendNotification {
//    /**
//     * Loại thông báo
//     */
//    EventType type();
//
//    /**
//     * Template ID cho nội dung thông báo
//     */
//    String templateId() default "";
//
//    /**
//     * Độ ưu tiên của thông báo
//     */
//    Priority priority() default Priority.MEDIUM;
//}