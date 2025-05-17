//package com.example.nhatrobackend.DTO.annotation;
//
//
//
//import com.example.nhatrobackend.DTO.NotificationEvent;
//import com.example.nhatrobackend.Entity.Field.EventType;
//import com.example.nhatrobackend.Entity.Field.Status;
//import com.example.nhatrobackend.Entity.Post;
//import com.example.nhatrobackend.Service.NotificationService;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//import static com.example.nhatrobackend.Entity.Field.EventType.POST_APPROVED;
//
///**
// * Aspect để xử lý các annotation SendNotification.
// * Class này tự động bắt các method được đánh dấu và gửi thông báo tương ứng.
// */
//@Aspect
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class NotificationAspect {
//
//    private final NotificationService notificationService;
//    private final Map<String, RentalNotificationTemplate> templateCache = new HashMap<>();
//
//    /**
//     * Xử lý các method được đánh dấu @SendNotification
//     * Tự động gửi thông báo sau khi method thực hiện xong
//     */
//    @Around("@annotation(sendNotification)")
//    public Object handleNotification(ProceedingJoinPoint joinPoint,
//                                     SendNotification sendNotification) throws Throwable {
//        // Thực hiện method gốc
//        Object result = joinPoint.proceed();
//
//        try {
//            // Xây dựng thông báo dựa trên kết quả và template
//            NotificationEvent event = buildNotificationEvent(result, sendNotification);
//
//            // Gửi thông báo
//            notificationService.sendNotification(event);
//
//        } catch (Exception e) {
//            log.error("Lỗi khi gửi thông báo: {}", e.getMessage());
//        }
//
//        return result;
//    }
//
//    /**
//     * Xây dựng nội dung thông báo dựa trên template và dữ liệu
//     */
//    private NotificationEvent buildNotificationEvent(Object result,
//                                                     SendNotification annotation) {
//        // Lấy template dựa trên templateId
//        RentalNotificationTemplate template = getTemplateById(annotation.templateId());
//
//        // Thay thế các placeholder trong template
//        String content = replacePlaceholders(template.content(), result);
//
//        return NotificationEvent.builder()
//                .eventId(UUID.randomUUID().toString())
//                .type(annotation.type())  // Bây giờ sẽ nhận EventType
//                .title(template.title())
//                .content(content)
//                .priority(annotation.priority())
//                .timestamp(LocalDateTime.now())
//                .status(Status.PENDING)
//                .metadata(createMetadata(result))
//                .build();
//    }
//
//    private Map<String, Object> createMetadata(Object result) {
//        Map<String, Object> metadata = new HashMap<>();
//        if (result instanceof Post) {
//            Post post = (Post) result;
//            metadata.put("postId", post.getPostId());
//            metadata.put("userId", post.getUser().getUserId());
//            if (post.getRoom() != null) {
//                metadata.put("roomPrice", post.getRoom().getPrice());
//            }
//        }
//        return metadata;
//    }
//
//    /**
//     * Lấy template theo ID
//     */
//    private RentalNotificationTemplate getTemplateById(String templateId) {
//        // Kiểm tra cache trước
//        if (templateCache.containsKey(templateId)) {
//            return templateCache.get(templateId);
//        }
//
//        // Nếu không có trong cache, tạo template mới
//        RentalNotificationTemplate template = createTemplate(templateId);
//        templateCache.put(templateId, template);
//        return template;
//    }
//
//    /**
//     * Tạo template dựa trên templateId
//     */
//    private RentalNotificationTemplate createTemplate(String templateId) {
//        switch (templateId) {
//            case "POST_APPROVAL":
//                return new RentalNotificationTemplate() {
//                    @Override
//                    public String value() {
//                        return null;
//                    }
//
//                    @Override
//                    public String templateId() {
//                        return "POST_APPROVAL";
//                    }
//
//                    @Override
//                    public String title() {
//                        return "Bài đăng đã được duyệt";
//                    }
//
//                    @Override
//                    public String content() {
//                        return "Bài đăng '{postTitle}' của bạn đã được duyệt";
//                    }
//
//                    @Override
//                    public EventType type() {  // Thay đổi kiểu trả về
//                        return POST_APPROVED;
//                    }
//
//                    @Override
//                    public String redirectUrl() {
//                        return "/posts/{postId}";
//                    }
//
//                    @Override
//                    public Class<? extends java.lang.annotation.Annotation> annotationType() {
//                        return RentalNotificationTemplate.class;
//                    }
//                };
//            // Thêm các case khác...
//            default:
//                throw new IllegalArgumentException("Template không tồn tại: " + templateId);
//        }
//    }
//
//    /**
//     * Thay thế các placeholder trong template với dữ liệu thực tế
//     */
//    private String replacePlaceholders(String template, Object data) {
//        if (data instanceof Post) {
//            Post post = (Post) data;
//            return template
//                    .replace("{postTitle}", post.getTitle())
//                    .replace("{postId}", String.valueOf(post.getPostId()))
//                    .replace("{userName}", post.getUser().getFullName())
//                    .replace("{roomPrice}", String.valueOf(post.getRoom().getPrice()));
//        }
//        // Thêm các trường hợp khác nếu cần
//
//        return template;
//    }
//
//    /**
//     * Tạo URL chuyển hướng với các tham số động
//     */
//    private String createRedirectUrl(String template, Object data) {
//        if (data instanceof Post) {
//            Post post = (Post) data;
//            return template.replace("{postId}", String.valueOf(post.getPostId()));
//        }
//        return template;
//    }
//}