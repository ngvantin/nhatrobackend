package com.example.nhatrobackend.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Cấu hình chung cho hệ thống thông báo.
 * Class này cung cấp các bean và cấu hình cần thiết cho:
 * - Xử lý JSON với các kiểu dữ liệu datetime
 * - Hỗ trợ lập lịch tác vụ (scheduling)
 * - Hỗ trợ AOP (Aspect-Oriented Programming)
 */
@Configuration  // Đánh dấu đây là class cấu hình Spring
@EnableScheduling  // Cho phép sử dụng các annotation @Scheduled trong ứng dụng
@EnableAspectJAutoProxy  // Kích hoạt AOP proxy
public class NotificationConfig {

    /**
     * Cấu hình ObjectMapper để xử lý JSON trong ứng dụng.
     * Bean này đặc biệt quan trọng cho hệ thống thông báo vì:
     * 1. Xử lý serialize/deserialize các đối tượng NotificationDTO
     * 2. Hỗ trợ chuyển đổi các trường datetime (LocalDateTime) trong thông báo
     * 3. Được sử dụng bởi RabbitMQ để chuyển đổi message
     * 4. Xử lý JSON trong REST API responses
     *
     * @return ObjectMapper đã được cấu hình với JavaTimeModule
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Disable writing dates as arrays
        return mapper;
    }

    // Có thể thêm các bean configuration khác cho notification system như:
    // - WebSocket configuration
    // - Notification cleanup scheduler
    // - Notification template engine
}