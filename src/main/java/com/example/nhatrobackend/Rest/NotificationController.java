package com.example.nhatrobackend.Rest;


import com.example.nhatrobackend.DTO.NotificationEvent;
import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.DTO.response.NotificationResponse;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller xử lý các endpoint liên quan đến thông báo
 */


import com.example.nhatrobackend.DTO.response.NotificationResponse;
import com.example.nhatrobackend.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthenticationFacade authenticationFacade;

    // API lấy danh sách thông báo của user
    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<NotificationResponse>>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Integer currentUserId = authenticationFacade.getCurrentUserId();

        Page<NotificationResponse> notifications = notificationService.getUserNotifications(currentUserId, page, size);

        return ResponseEntity.ok(ResponseWrapper.<Page<NotificationResponse>>builder()
                .status("success")
                .data(notifications)
                .message("Danh sách thông báo của người dùng")
                .build());
    }

    // API đánh dấu tất cả thông báo đã đọc
    @PostMapping("/mark-all-read")
    public ResponseEntity<ResponseWrapper<Void>> markAllAsRead() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = Integer.parseInt(auth.getName());

        notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Đã đánh dấu tất cả thông báo là đã đọc")
                .build());
    }

    /**
     * Endpoint để client subscribe nhận notifications qua SSE
     */
    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationResponse>> subscribeToNotifications(@PathVariable Integer userId) {
        return notificationService.subscribeToUserNotifications(userId)
                .map(notification -> ServerSentEvent.<NotificationResponse>builder()
                        .id(String.valueOf(System.currentTimeMillis()))
                        .event("notification")
                        .data(notification)
                        .build());
    }

    /**
     * Đánh dấu thông báo đã đọc
     */
    @PostMapping("/{notificationId}/read")
    public void markNotificationAsRead(@PathVariable String notificationId) {
        notificationService.markNotificationAsRead(notificationId);
    }
}
//@Slf4j
//@RestController
//@RequestMapping("/api/notifications")
//@RequiredArgsConstructor
//public class NotificationController {
//
////     private final NotificationService notificationService;
//
////     /**
////      * Lấy danh sách thông báo của user hiện tại
////      */
////     @GetMapping
////     public ResponseEntity<ResponseWrapper<Flux<NotificationResponse>>> getUserNotifications(
////             @RequestParam(defaultValue = "0") int page,
////             @RequestParam(defaultValue = "10") int size) {
//
////         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
////         Integer userId = Integer.parseInt(auth.getName());
//
////         Flux<NotificationResponse> notifications = notificationService.getUserNotifications(userId, page, size);
//
////         return ResponseEntity.ok(ResponseWrapper.<Flux<NotificationResponse>>builder()
////                 .status("success")
////                 .data(notifications)
////                 .message("Danh sách thông báo của người dùng")
////                 .build());
////     }
//
////     /**
////      * Đánh dấu thông báo đã đọc
////      */
////     @PutMapping("/{notificationId}/read")
////     public ResponseEntity<ResponseWrapper<Mono<Void>>> markAsRead(@PathVariable Long notificationId) {
////         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
////         Integer userId = Integer.parseInt(auth.getName());
//
////         Mono<Void> result = notificationService.markAsRead(notificationId, userId);
//
////         return ResponseEntity.ok(ResponseWrapper.<Mono<Void>>builder()
////                 .status("success")
////                 .data(result)
////                 .message("Đã đánh dấu thông báo là đã đọc")
////                 .build());
////     }
//
////     /**
////      * Gửi thông báo duyệt bài đăng (Admin only)
////      */
////     @PostMapping("/post-approval/{postId}")
////     public ResponseEntity<ResponseWrapper<Mono<NotificationResponse>>> sendPostApprovalNotification(
////             @PathVariable Integer postId) {
//
////         Mono<NotificationResponse> notification = notificationService.sendPostApprovalNotification(postId);
//
////         return ResponseEntity.ok(ResponseWrapper.<Mono<NotificationResponse>>builder()
////                 .status("success")
////                 .data(notification)
////                 .message("Đã gửi thông báo duyệt bài đăng")
////                 .build());
////     }
//
////     /**
////      * Gửi thông báo tùy chỉnh
////      */
////     @PostMapping("/send")
////     public ResponseEntity<ResponseWrapper<Mono<Void>>> sendNotification(
////             @RequestBody NotificationEvent event) {
//
////         Mono<Void> result = notificationService.sendNotification(event);
//
////         return ResponseEntity.ok(ResponseWrapper.<Mono<Void>>builder()
////                 .status("success")
////                 .data(result)
////                 .message("Đã gửi thông báo thành công")
////                 .build());
////     }
//}