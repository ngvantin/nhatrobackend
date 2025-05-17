//package com.example.nhatrobackend.Config;
//
//import io.netty.channel.ChannelOption;
//import io.netty.handler.timeout.ReadTimeoutHandler;
//import io.netty.handler.timeout.WriteTimeoutHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.netty.http.client.HttpClient;
//
//import java.time.Duration;
//import java.util.concurrent.TimeUnit;
//
///**
// * Cấu hình WebClient cho các HTTP requests reactive.
// * Class này thiết lập một HTTP client non-blocking để:
// * - Gọi các external APIs (như notification services)
// * - Xử lý các requests/responses một cách bất đồng bộ
// * - Tối ưu hiệu suất với non-blocking I/O
// */
//@Configuration
//public class WebClientConfig {
//
//    /**
//     * Tạo WebClient.Builder với các cấu hình tùy chỉnh.
//     * Cấu hình bao gồm:
//     * 1. Timeout settings:
//     *    - Connection timeout: 5 giây
//     *    - Response timeout: 5 giây
//     *    - Read timeout: 5 giây
//     *    - Write timeout: 5 giây
//     * 2. Memory settings:
//     *    - Max in memory size: 16MB
//     *
//     * Các timeout này quan trọng cho:
//     * - Tránh request bị treo
//     * - Xử lý lỗi khi external service không phản hồi
//     * - Quản lý tài nguyên hiệu quả
//     *
//     * Trong context nhà trọ, WebClient được sử dụng để:
//     * - Gửi notifications đến các external services
//     * - Tích hợp với các third-party APIs
//     * - Xử lý webhook callbacks
//     *
//     * @return WebClient.Builder đã được cấu hình
//     */
//    @Bean
//    public WebClient.Builder webClientBuilder() {
//        // Cấu hình HTTP client với các timeout
//        HttpClient httpClient = HttpClient.create()
//                // Thiết lập connection timeout (5 giây)
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
//                // Thiết lập response timeout (5 giây)
//                .responseTimeout(Duration.ofMillis(5000))
//                // Cấu hình handlers cho connection
//                .doOnConnected(conn -> conn
//                        // Thêm handler cho read timeout (5 giây)
//                        .addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
//                        // Thêm handler cho write timeout (5 giây)
//                        .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));
//
//        // Tạo và cấu hình WebClient.Builder
//        return WebClient.builder()
//                // Kết nối với HTTP client đã cấu hình
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                // Cấu hình kích thước buffer tối đa (16MB)
//                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024));
//    }
//}
