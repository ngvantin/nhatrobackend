//package com.example.nhatrobackend.Service;
//
//
//import com.example.nhatrobackend.DTO.NotificationEvent;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
///**
// * Service xử lý việc gửi thông báo qua webhook.
// * Được sử dụng để tích hợp với các hệ thống bên ngoài như:
// * - Mobile app notifications
// * - Email service
// * - SMS service
// */
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class WebhookService {
//    private final WebClient.Builder webClientBuilder;
//
//    /**
//     * Gửi thông báo qua webhook
//     * @param event Sự kiện thông báo cần gửi
//     * @return Mono<Boolean> kết quả gửi
//     */
//    public Mono<Boolean> sendWebhookNotification(NotificationEvent event) {
//        // Xác định URL webhook dựa trên loại thông báo
//        String webhookUrl = determineWebhookUrl(String.valueOf(event.getType()));
//
//        return webClientBuilder.build()
//                .post()
//                .uri(webhookUrl)
//                .bodyValue(event)
//                .retrieve()
//                .bodyToMono(Void.class)
//                .thenReturn(true)
//                .onErrorResume(e -> {
//                    log.error("Lỗi khi gửi webhook: {}", e.getMessage());
//                    return Mono.just(false);
//                });
//    }
//
//    /**
//     * Xác định URL webhook dựa trên loại thông báo
//     */
//    private String determineWebhookUrl(String type) {
//        switch (type) {
//            case "POST_APPROVAL":
//                return "https://api.example.com/webhooks/post-approval";
//            case "ROOM_INTEREST":
//                return "https://api.example.com/webhooks/room-interest";
//            default:
//                return "https://api.example.com/webhooks/default";
//        }
//    }
//}
