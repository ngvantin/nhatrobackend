package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.NotificationEvent;
import com.example.nhatrobackend.DTO.request.NotificationRequest;
import com.example.nhatrobackend.DTO.response.NotificationResponse;
import com.example.nhatrobackend.Entity.Notification;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface định nghĩa các phương thức xử lý thông báo trong hệ thống nhà trọ.
 * Cung cấp các operation cơ bản và nâng cao cho việc quản lý thông báo.
 */
public interface NotificationService {
    // Lấy danh sách thông báo của user
    Page<NotificationResponse> getUserNotifications(Integer userId, int page, int size);
    // Đánh dấu tất cả thông báo đã đọc
    void markAllAsRead(Integer userId);

    void sendNotification(NotificationEvent event);
    Flux<NotificationResponse> subscribeToUserNotifications(Integer userId);
//    /**
//     * Gửi thông báo cho một user cụ thể
//     */
//    void sendNotification(NotificationEvent event);
//
//    /**
//     * Subscribe để nhận thông báo real-time cho một user
//     */
//    Flux<NotificationResponse> subscribeToUserNotifications(Integer userId);
//
//    /**
//     * Đánh dấu thông báo đã đọc
//     */
    void markNotificationAsRead(String notificationId);

    Notification save(Notification notification);
//
//    /**
//     * Lấy danh sách thông báo của một user
//     * @param userId ID của user cần lấy thông báo
//     * @param page Số trang
//     * @param size Số lượng thông báo mỗi trang
//     */
//    Flux<NotificationResponse> getUserNotifications(Integer userId, int page, int size);
//
//    /**
//     * Đánh dấu thông báo đã đọc
//     * @param notificationId ID của thông báo
//     * @param userId ID của user
//     */
//    Mono<Void> markAsRead(Long notificationId, Integer userId);
//
//    /**
//     * Gửi thông báo duyệt bài đăng
//     * @param postId ID bài đăng được duyệt
//     */
//    Mono<NotificationResponse> sendPostApprovalNotification(Integer postId);
}
