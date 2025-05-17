package com.example.nhatrobackend.DTO.request;

import lombok.*;

/**
 * DTO để nhận request tạo thông báo mới.
 * Sử dụng cho các trường hợp:
 * - Admin tạo thông báo hệ thống
 * - Hệ thống tự động tạo thông báo khi duyệt bài
 * - Thông báo cho chủ trọ khi có người quan tâm phòng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String title;
    private String content;
    private String type;
    private Integer userId;
    private Integer postId;
    private String redirectUrl;
}
