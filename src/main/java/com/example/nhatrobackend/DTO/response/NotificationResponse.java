package com.example.nhatrobackend.DTO.response;


import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO để trả về thông tin thông báo cho client.
 * Được sử dụng trong:
 * - API lấy danh sách thông báo
 * - Server-Sent Events để push thông báo realtime
 * - WebSocket để gửi thông báo tức thì
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String title;
    private String content;
    private String type;
    private Integer userId;
    private Integer postId;
    private LocalDateTime createdAt;
    private boolean isRead;
    private String redirectUrl;
}