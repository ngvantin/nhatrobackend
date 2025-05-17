package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.DTO.NotificationEvent;
import com.example.nhatrobackend.DTO.request.NotificationRequest;
import com.example.nhatrobackend.DTO.response.NotificationResponse;
import com.example.nhatrobackend.Entity.Field.EventType;
import com.example.nhatrobackend.Entity.Notification;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Responsitory.NotificationRepository;
import com.example.nhatrobackend.Responsitory.PostRepository;
import com.example.nhatrobackend.Responsitory.UserRepository;
import com.example.nhatrobackend.Service.NotificationService;
//import com.example.nhatrobackend.Service.WebhookService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.nhatrobackend.Config.RabbitMQConfig.NOTIFICATION_EXCHANGE;
import static com.example.nhatrobackend.Config.RabbitMQConfig.NOTIFICATION_ROUTING_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final RabbitTemplate rabbitTemplate;
    private final Map<Integer, Sinks.Many<NotificationResponse>> userSinks = new ConcurrentHashMap<>();

    @Override
    public void sendNotification(NotificationEvent event) {
        try {
            log.info("Sending notification event: {}", event);

            // Kiểm tra xem notification có ID không
            if (event.getNotification().getId() == null) {
                log.warn("Notification ID is null!");
            }

            rabbitTemplate.convertAndSend(
                    NOTIFICATION_EXCHANGE,
                    NOTIFICATION_ROUTING_KEY,
                    event
            );

            Integer userId = event.getNotification().getUserId();
            if (userSinks.containsKey(userId)) {
                log.info("Sending to SSE sink for user {}", userId);
                userSinks.get(userId).tryEmitNext(event.getNotification());
            } else {
                log.info("No SSE sink found for user {}", userId);
            }

            log.info("Notification sent successfully");
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    @Override
    public Flux<NotificationResponse> subscribeToUserNotifications(Integer userId) {
        return userSinks.computeIfAbsent(
                userId,
                k -> Sinks.many().multicast().onBackpressureBuffer()
        ).asFlux();
    }

        @Override
    public void markNotificationAsRead(String notificationId) {
        // Implementation for marking notification as read
        // This would typically update a notification status in database
        log.info("Marking notification as read: {}", notificationId);
    }

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }
}
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class NotificationServiceImpl implements NotificationService {
//    private final NotificationRepository notificationRepository;
//    private final RabbitTemplate rabbitTemplate;
////    private final WebhookService webhookService;
//    private final PostRepository postRepository;
//    private final UserRepository userRepository;
//
//    // Sinks for each user to handle SSE connections
//    private final Map<Integer, Sinks.Many<NotificationResponse>> userSinks = new ConcurrentHashMap<>();
//
//    // RabbitMQ configuration
//    private static final String NOTIFICATION_EXCHANGE = "notification.exchange";
//    private static final String NOTIFICATION_ROUTING_KEY = "notification.event";
//
//    @Override
//    public Mono<NotificationResponse> sendPostApprovalNotification(Integer postId) {
//        return Mono.fromCallable(() -> {
//            // Tạo thông báo duyệt bài
//            NotificationRequest request = NotificationRequest.builder()
//                    .type("POST_APPROVAL")
//                    .title("Bài đăng đã được duyệt")
//                    .content("Bài đăng của bạn đã được admin phê duyệt")
//                    .postId(postId)
//                    .build();
//
//            // Lưu vào database
//            Notification notification = saveNotification(request);
//
//            // Gửi event qua RabbitMQ
//            sendNotification(NotificationEvent.builder()
//                    .eventId(UUID.randomUUID().toString())
//                    .type(EventType.valueOf(notification.getType()))
//                    .notification(mapToResponse(notification))
//                    .timestamp(LocalDateTime.now())
//                    .build());
//
//            return mapToResponse(notification);
//        }).subscribeOn(Schedulers.boundedElastic());
//    }
//
//    @Override
//    public void sendNotification(NotificationEvent event) {
//        try {
//            // Gửi event tới RabbitMQ
//            rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, NOTIFICATION_ROUTING_KEY, event);
//
//            // Nếu có sink cho user này, gửi notification qua SSE
//            Integer userId = event.getNotification().getUserId();
//            if (userSinks.containsKey(userId)) {
//                userSinks.get(userId).tryEmitNext(event.getNotification());
//            }
//
//            log.info("Sent notification event: {}", event);
//        } catch (Exception e) {
//            log.error("Error sending notification: {}", e.getMessage(), e);
//            throw new RuntimeException("Failed to send notification", e);
//        }
//    }
//
//    /**
//     * Lấy danh sách thông báo của user
//     */
//    @Override
//    public Flux<NotificationResponse> getUserNotifications(Integer userId, int page, int size) {
//        return Flux.fromIterable(
//                notificationRepository.findByUserIdOrderByCreatedAtDesc(
//                        userId,
//                        PageRequest.of(page, size)
//                ).getContent()
//        ).map(this::mapToResponse);
//    }
//
//    /**
//     * Đánh dấu thông báo đã đọc
//     */
//    @Override
//    public Mono<Void> markAsRead(Long notificationId, Integer userId) {
//        return Mono.fromRunnable(() -> {
//            Notification notification = notificationRepository.findById(notificationId)
//                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông báo"));
//
//            if (!notification.getUserId().equals(userId)) {
//                throw new AccessDeniedException("Không có quyền truy cập thông báo này");
//            }
//
//            notification.setRead(true);
//            notification.setUpdatedAt(LocalDateTime.now());
//            notificationRepository.save(notification);
//        }).subscribeOn(Schedulers.boundedElastic()).then();
//    }
//
//    @Override
//    public Flux<NotificationResponse> subscribeToUserNotifications(Integer userId) {
//        // Create new sink if not exists
//        Sinks.Many<NotificationResponse> sink = userSinks.computeIfAbsent(userId,
//                k -> Sinks.many().multicast().onBackpressureBuffer());
//
//        return sink.asFlux();
//    }
//
//    @Override
//    public void markNotificationAsRead(String notificationId) {
//        // Implementation for marking notification as read
//        // This would typically update a notification status in database
//        log.info("Marking notification as read: {}", notificationId);
//    }
//
//    // Helper methods
//
//    /**
//     * Lưu thông báo vào database
//     */
//    private Notification saveNotification(NotificationRequest request) {
//        Notification notification = Notification.builder()
//                .title(request.getTitle())
//                .content(request.getContent())
//                .type(request.getType())
//                .userId(request.getUserId())
//                .postId(request.getPostId())
//                .redirectUrl(request.getRedirectUrl())
//                .isRead(false)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        return notificationRepository.save(notification);
//    }
//
//    /**
//     * Chuyển đổi từ Entity sang Response DTO
//     */
//    private NotificationResponse mapToResponse(Notification notification) {
//        return NotificationResponse.builder()
//                .id(notification.getId())
//                .title(notification.getTitle())
//                .content(notification.getContent())
//                .type(notification.getType())
//                .userId(notification.getUserId())
//                .postId(notification.getPostId())
//                .createdAt(notification.getCreatedAt())
//                .isRead(notification.isRead())
//                .redirectUrl(notification.getRedirectUrl())
//                .build();
//    }
//
//    /**
//     * Xóa các thông báo cũ
//     * Được gọi bởi scheduled task
//     */
//    @Scheduled(cron = "0 0 1 * * ?") // Chạy lúc 1 giờ sáng mỗi ngày
//    public void cleanupOldNotifications() {
//        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30); // Xóa thông báo cũ hơn 30 ngày
//        notificationRepository.deleteOldNotifications(cutoffDate);
//    }
//}
