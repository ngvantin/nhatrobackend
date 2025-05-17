package com.example.nhatrobackend.Entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity chính cho thông báo trong hệ thống nhà trọ.
 * Được sử dụng để lưu trữ các loại thông báo khác nhau như:
 * - Thông báo duyệt/từ chối bài đăng
 * - Thông báo duyệt tài khoản chủ trọ
 * - Thông báo bài đăng bị báo cáo
 * - Thông báo chào mừng người dùng mới
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    /**
     * Loại thông báo trong hệ thống nhà trọ:
     * POST_APPROVAL - Duyệt bài đăng
     * POST_REJECTION - Từ chối bài đăng
     * POST_REPORT - Bài đăng bị báo cáo
     * LANDLORD_APPROVAL - Duyệt tài khoản chủ trọ
     * WELCOME - Chào mừng người dùng mới
     * SYSTEM - Thông báo hệ thống
     */
    @Column(name = "notification_type")
    private String type;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "post_id")
    private Integer postId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(name = "is_read")
    private boolean isRead;

    /**
     * URL để chuyển hướng khi click vào thông báo
     * Ví dụ: /posts/{postId} cho thông báo bài đăng
     */
    private String redirectUrl;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isRead = false;
    }
}