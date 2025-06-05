package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.DTO.NotificationEvent;
import com.example.nhatrobackend.DTO.request.NotificationRequest;
import com.example.nhatrobackend.DTO.response.NotificationResponse;
import com.example.nhatrobackend.Entity.Field.EventType;
import com.example.nhatrobackend.Entity.Field.Status;
import com.example.nhatrobackend.Entity.Notification;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Entity.Follower;
import com.example.nhatrobackend.Responsitory.NotificationRepository;
import com.example.nhatrobackend.Responsitory.PostRepository;
import com.example.nhatrobackend.Responsitory.UserRepository;
import com.example.nhatrobackend.Service.NotificationService;
import com.example.nhatrobackend.Service.UserService;
//import com.example.nhatrobackend.Service.WebhookService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.Set;
import java.util.Optional;

import static com.example.nhatrobackend.Config.RabbitMQConfig.NOTIFICATION_EXCHANGE;
import static com.example.nhatrobackend.Config.RabbitMQConfig.NOTIFICATION_ROUTING_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;
//    private final UserService userService;

    private final Map<Integer, Sinks.Many<NotificationResponse>> userSinks = new ConcurrentHashMap<>();

    @Override
    public Page<NotificationResponse> getUserNotifications(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return notifications.map(this::mapToResponse);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .userId(notification.getUserId())
                .postId(notification.getPostId())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.isRead())
                .redirectUrl(notification.getRedirectUrl())
                .build();
    }
    @Override
    public void markAllAsRead(Integer userId) {
        try {
            notificationRepository.updateIsReadByUserIdAndIsRead(userId, true);
            log.info("Marked all notifications as read for user: {}", userId);
        } catch (Exception e) {
            log.error("Error marking all notifications as read for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to mark all notifications as read", e);
        }
    }

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
    public void markNotificationAsRead(Long notificationId) {
        try {
            // Verify notification exists
            if (!notificationRepository.existsById(notificationId)) {
                throw new EntityNotFoundException("Notification not found with id: " + notificationId);
            }
            
            notificationRepository.markNotificationAsRead(notificationId);
            log.info("Marked notification as read: {}", notificationId);
        } catch (EntityNotFoundException e) {
            log.error("Notification not found: {}", notificationId);
            throw e;
        } catch (Exception e) {
            log.error("Error marking notification as read {}: {}", notificationId, e.getMessage());
            throw new RuntimeException("Failed to mark notification as read", e);
        }
    }

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public void sendNewPostNotificationToFollowers(Integer authorId, Integer postId, String postTitle) {
        log.info("Sending new post notification to followers of author: {}", authorId);

        // Tìm người đăng bài viết
        Optional<User> optionalAuthor = userRepository.findById(authorId);
        if (optionalAuthor.isEmpty()) {
            log.warn("Author not found with ID: {}", authorId);
            return; // Không tìm thấy tác giả, dừng xử lý
        }
        User author = optionalAuthor.get();
        Set<Follower> followers = author.getFollowers(); // Lấy danh sách người theo dõi

        if (followers.isEmpty()) {
            log.info("Author {} has no followers to notify.", authorId);
            return; // Không có người theo dõi, dừng xử lý
        }

        String redirectUrl = String.format("/posts/%d", postId);
        String notificationTitle = "Bài viết mới từ " + author.getFullName();
        String notificationContent = author.getFullName() + " vừa đăng bài viết mới: \"" + postTitle + "\"";

        for (Follower follower : followers) {
            User followerUser = follower.getFollowingUser(); // Đây là người đang theo dõi

            // Tạo notification cho từng người theo dõi
            Notification notification = Notification.builder()
                    .title(notificationTitle)
                    .content(notificationContent)
                    .type(EventType.NEW_POST_FROM_FOLLOWING.name()) // Sử dụng EventType phù hợp
                    .userId(followerUser.getUserId()) // ID của người theo dõi
                    .postId(postId)
                    .redirectUrl(redirectUrl)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            // Lưu notification vào database
            Notification savedNotification = notificationRepository.save(notification);
            log.info("Saved notification for follower {}: {}", followerUser.getUserId(), savedNotification.getId());

            // Tạo và gửi notification event
            NotificationResponse notificationResponse = mapToResponse(savedNotification);
            NotificationEvent event = NotificationEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .type(EventType.NEW_POST_FROM_FOLLOWING) // Sử dụng EventType phù hợp
                    .notification(notificationResponse)
                    .timestamp(LocalDateTime.now())
                    .metadata(Map.of(
                            "postId", postId,
                            "authorId", authorId,
                            "authorName", author.getFullName(),
                            "followerId", followerUser.getUserId()
                    ))
                    .priority(NotificationEvent.Priority.MEDIUM) // Priority có thể tùy chỉnh
                    .status(Status.PENDING) // Status có thể tùy chỉnh
                    .build();

            // Gửi notification (qua RabbitMQ và SSE)
            sendNotification(event);
            log.info("Sent notification event for follower {}: {}", followerUser.getUserId(), event.getEventId());
        }
        log.info("Finished sending new post notifications to followers of author: {}", authorId);
    }

    @Override
    public long getUnreadNotificationCountForUser(Integer userId) {
        // Use the repository method to count unread notifications for the given user ID
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }
}

