//package com.example.nhatrobackend.Config;
//
//
//import com.example.nhatrobackend.DTO.NotificationEvent;
//import com.example.nhatrobackend.Service.WebhookService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
///**
// * Consumer để xử lý các sự kiện thông báo từ RabbitMQ.
// * Class này đảm nhiệm việc:
// * 1. Lắng nghe các thông báo từ RabbitMQ queue
// * 2. Xử lý và gửi thông báo đến các webhook đã đăng ký
// * 3. Xử lý các trường hợp lỗi và retry
// *
// * Trong context nhà trọ, consumer này xử lý:
// * - Thông báo duyệt/từ chối bài đăng
// * - Thông báo duyệt tài khoản chủ trọ
// * - Thông báo có người quan tâm phòng trọ
// * - Các thông báo hệ thống khác
// */
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class NotificationConsumer {
//
//    private final WebhookService webhookService;
//
//    /**
//     * Lắng nghe và xử lý các sự kiện thông báo từ RabbitMQ.
//     *
//     * Các loại sự kiện trong hệ thống nhà trọ:
//     * - POST_APPROVED: Admin duyệt bài đăng
//     * - POST_REJECTED: Admin từ chối bài đăng
//     * - POST_REPORTED: Bài đăng bị báo cáo
//     * - LANDLORD_APPROVED: Duyệt tài khoản chủ trọ
//     * - ROOM_INTERESTED: Có người quan tâm phòng
//     *
//     * @param event Sự kiện thông báo cần xử lý
//     */
//    @RabbitListener(queues = "${rabbitmq.queue.notification:notification-queue}")
//    public void handleNotificationEvent(NotificationEvent event) {
//        log.info("Nhận được sự kiện thông báo từ RabbitMQ: {}", event);
//
//        // Gửi thông báo đến webhook và nhận kết quả dạng reactive
//        Mono<Boolean> result = webhookService.sendWebhookNotification(event);
//
//        // Xử lý kết quả gửi webhook bất đồng bộ
//        result.subscribe(
//                success -> {
//                    if (success) {
//                        log.info("Gửi thông báo đến webhook thành công: {}", event.getEventId());
//                    } else {
//                        log.warn("Gửi thông báo đến webhook thất bại: {}", event.getEventId());
//                        handleFailedNotification(event);
//                    }
//                },
//                error -> {
//                    log.error("Lỗi khi gửi thông báo đến webhook: {}", error.getMessage());
//                    handleFailedNotification(event);
//                }
//        );
//    }
//
//    /**
//     * Xử lý các thông báo gửi thất bại.
//     * Trong hệ thống nhà trọ, việc này rất quan trọng vì:
//     * - Đảm bảo chủ trọ/người dùng không bỏ lỡ thông báo quan trọng
//     * - Cho phép retry các thông báo quan trọng (như duyệt bài, thanh toán)
//     * - Giúp tracking và debug các vấn đề về thông báo
//     *
//     * @param event Sự kiện thông báo thất bại cần xử lý
//     */
//    private void handleFailedNotification(NotificationEvent event) {
//        // TODO: Implement retry mechanism cho các thông báo quan trọng
//        // Các strategy có thể áp dụng:
//        // 1. Lưu vào dead-letter queue để xử lý sau
//        // 2. Gửi alert đến hệ thống monitoring
//        // 3. Lưu vào database với trạng thái FAILED để retry định kỳ
//        // 4. Gửi email thông báo cho admin nếu là thông báo quan trọng
//
//        log.warn("Đánh dấu sự kiện thông báo {} để xử lý lại sau", event.getEventId());
//    }
//}
