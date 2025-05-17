//package com.example.nhatrobackend.Rest;
//
//
//import com.example.nhatrobackend.DTO.response.NotificationResponse;
//import com.example.nhatrobackend.Service.NotificationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * Controller xử lý Server-Sent Events (SSE) cho thông báo realtime.
// * Cho phép:
// * - Thiết lập kết nối SSE với client
// * - Gửi thông báo realtime
// * - Quản lý các kết nối SSE
// */
//@RestController
//@RequestMapping("/api/notifications/sse")
//@RequiredArgsConstructor
//@Slf4j
//public class NotificationSSEController {
//
//    private final NotificationService notificationService;
//    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();
//
//    /**
//     * Thiết lập kết nối SSE cho user
//     * @param userId ID của user đang đăng nhập
//     */
//    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter connect(@AuthenticationPrincipal Integer userId) {
//        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
//
//        // Xử lý khi kết nối timeout
//        emitter.onTimeout(() -> {
//            emitters.remove(userId);
//            emitter.complete();
//        });
//
//        // Xử lý khi kết nối đóng
//        emitter.onCompletion(() -> {
//            emitters.remove(userId);
//        });
//
//        // Lưu emitter vào map
//        emitters.put(userId, emitter);
//
//        // Gửi event kết nối thành công
//        try {
//            emitter.send(SseEmitter.event()
//                    .name("connect")
//                    .data("Kết nối SSE thành công"));
//        } catch (IOException e) {
//            emitter.completeWithError(e);
//        }
//
//        return emitter;
//    }
//
//    /**
//     * Gửi thông báo tới một user cụ thể
//     */
//    public void sendNotificationToUser(Integer userId, NotificationResponse notification) {
//        SseEmitter emitter = emitters.get(userId);
//        if (emitter != null) {
//            try {
//                emitter.send(SseEmitter.event()
//                        .name("notification")
//                        .data(notification));
//            } catch (IOException e) {
//                emitters.remove(userId);
//                emitter.completeWithError(e);
//            }
//        }
//    }
//
//    /**
//     * Gửi thông báo tới tất cả chủ trọ
//     */
//    public void broadcastToLandlords(NotificationResponse notification) {
//        emitters.forEach((userId, emitter) -> {
//            try {
//                // Kiểm tra nếu user là chủ trọ
//                if (isLandlord(userId)) {
//                    emitter.send(SseEmitter.event()
//                            .name("landlord-notification")
//                            .data(notification));
//                }
//            } catch (IOException e) {
//                emitters.remove(userId);
//                emitter.completeWithError(e);
//            }
//        });
//    }
//
//    /**
//     * Gửi thông báo tới tất cả admin
//     */
//    public void broadcastToAdmins(NotificationResponse notification) {
//        emitters.forEach((userId, emitter) -> {
//            try {
//                if (isAdmin(userId)) {
//                    emitter.send(SseEmitter.event()
//                            .name("admin-notification")
//                            .data(notification));
//                }
//            } catch (IOException e) {
//                emitters.remove(userId);
//                emitter.completeWithError(e);
//            }
//        });
//    }
//
//    // Helper methods
//    private boolean isLandlord(Integer userId) {
//        // TODO: Implement logic to check if user is landlord
//        return true;
//    }
//
//    private boolean isAdmin(Integer userId) {
//        // TODO: Implement logic to check if user is admin
//        return true;
//    }
//}
