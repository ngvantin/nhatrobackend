package com.example.nhatrobackend.DTO;


import com.example.nhatrobackend.DTO.request.NotificationRequest;
import com.example.nhatrobackend.DTO.response.NotificationResponse;
import com.example.nhatrobackend.Entity.Field.EventType;
import com.example.nhatrobackend.Entity.Field.Status;
import lombok.*;

/**
 * Event object cho xử lý thông báo bất đồng bộ.
 * Được sử dụng trong các trường hợp:
 * - Gửi thông báo qua RabbitMQ
 * - Xử lý webhook callbacks
 * - Trigger các action liên quan đến thông báo
 */
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class NotificationEvent {
//    private String eventId;
//    private EventType eventType;
//    private NotificationRequest notification;
//    private Object data; // Dữ liệu bổ sung (có thể là Post, User, etc.)
//}


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Class đại diện cho một sự kiện thông báo trong hệ thống.
 * Được sử dụng để truyền thông tin thông báo qua RabbitMQ và WebSocket.
 * Trong context nhà trọ, event này được tạo khi:
 * - Admin duyệt/từ chối bài đăng
 * - Có người quan tâm phòng trọ
 * - Duyệt tài khoản chủ trọ
 * - Cập nhật thông tin phòng trọ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    /**
     * ID duy nhất của event
     */
    private String eventId;
    private String title;
    private String content;
    private String redirectUrl;

    /**
     * Loại của event
     */
    private EventType type;

    /**
     * Thông tin chi tiết của thông báo
     */
    private NotificationResponse notification;

    /**
     * Thời gian tạo event
     */
    private LocalDateTime timestamp;

    /**
     * Dữ liệu bổ sung của event, có thể chứa:
     * - postId: ID bài đăng
     * - userId: ID người dùng
     * - roomPrice: Giá phòng mới
     * - reason: Lý do từ chối
     * - status: Trạng thái mới
     */
    private Map<String, Object> metadata;

    /**
     * Mức độ ưu tiên của thông báo
     */
    public enum Priority {
        HIGH,    // Ví dụ: Duyệt/từ chối bài đăng
        MEDIUM,  // Ví dụ: Có người quan tâm phòng
        LOW      // Ví dụ: Cập nhật thông tin
    }

    private Priority priority;

    /**
     * Trạng thái của event
     */
    private Status status;

    /**
     * Số lần thử lại nếu gửi thất bại
     */
    private int retryCount;

    /**
     * Thông tin lỗi nếu có
     */
    private String errorMessage;

    /**
     * Builder pattern cho việc tạo event dễ dàng hơn
     */
    public static class EventBuilder {
        /**
         * Tạo event duyệt bài đăng
         */
        public static NotificationEvent createPostApprovalEvent(NotificationResponse notification, Integer postId) {
            return NotificationEvent.builder()
                    .eventId(java.util.UUID.randomUUID().toString())
                    .type(EventType.POST_APPROVED)
                    .notification(notification)
                    .timestamp(LocalDateTime.now())
                    .metadata(Map.of("postId", postId))
                    .priority(Priority.HIGH)
                    .status(Status.PENDING)
                    .build();
        }

        /**
         * Tạo event có người quan tâm phòng
         */
        public static NotificationEvent createRoomInterestEvent(NotificationResponse notification,
                                                                Integer postId,
                                                                Integer interestedUserId) {
            return NotificationEvent.builder()
                    .eventId(java.util.UUID.randomUUID().toString())
                    .type(EventType.ROOM_INTERESTED)
                    .notification(notification)
                    .timestamp(LocalDateTime.now())
                    .metadata(Map.of(
                            "postId", postId,
                            "interestedUserId", interestedUserId
                    ))
                    .priority(Priority.MEDIUM)
                    .status(Status.PENDING)
                    .build();
        }

        /**
         * Tạo event cập nhật giá phòng
         */
        public static NotificationEvent createPriceUpdateEvent(NotificationResponse notification,
                                                               Integer postId,
                                                               Double newPrice) {
            return NotificationEvent.builder()
                    .eventId(java.util.UUID.randomUUID().toString())
                    .type(EventType.PRICE_UPDATED)
                    .notification(notification)
                    .timestamp(LocalDateTime.now())
                    .metadata(Map.of(
                            "postId", postId,
                            "newPrice", newPrice
                    ))
                    .priority(Priority.LOW)
                    .status(Status.PENDING)
                    .build();
        }
    }

    /**
     * Kiểm tra xem event có cần retry không
     */
    public boolean shouldRetry() {
        return status == Status.FAILED && retryCount < 3;
    }

    /**
     * Tăng số lần retry
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }

    /**
     * Đánh dấu event đã xử lý thành công
     */
    public void markAsProcessed() {
        this.status = Status.PROCESSED;
    }

    /**
     * Đánh dấu event xử lý thất bại
     */
    public void markAsFailed(String error) {
        this.status = Status.FAILED;
        this.errorMessage = error;
    }
}