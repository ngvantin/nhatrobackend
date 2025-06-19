    package com.example.nhatrobackend.Config;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.messaging.converter.DefaultContentTypeResolver;
    import org.springframework.messaging.converter.MappingJackson2MessageConverter;
    import org.springframework.messaging.converter.MessageConverter;
    import org.springframework.messaging.simp.config.MessageBrokerRegistry;
    import org.springframework.util.MimeTypeUtils;
    import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
    import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
    import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

    import java.util.List;

    // Message broker là trung gian xử lý và định tuyến các tin nhắn trong ứng dụng WebSocket. Nó quyết định cách tin nhắn được gửi đến các client khác nhau.
    @Configuration
    @EnableWebSocketMessageBroker
    public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

        //  cấu hình message broker.
        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.enableSimpleBroker("/user");
            registry.setApplicationDestinationPrefixes("/app");
            registry.setUserDestinationPrefix("/user");
        }

        //  đăng ký các STOMP endpoints.
        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/ws") // endpoint STOMP
                    .setAllowedOriginPatterns("*") // hoặc "https://fe-timkiemtro.vercel.app"
                    .withSockJS(); // Kích hoạt SockJS fallback
        }

        // cấu hình các message converters.
        @Override
        public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
            DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
            resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
            MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
            converter.setObjectMapper(new ObjectMapper());
            converter.setContentTypeResolver(resolver);
            messageConverters.add(converter);
            return false;
        }
    //
    //    @Override
    //    public void configureMessageBroker(MessageBrokerRegistry config) {
    //        config.enableSimpleBroker("/topic");
    //        config.setApplicationDestinationPrefixes("/app");
    //    }
    //
    //    @Override
    //    public void registerStompEndpoints(StompEndpointRegistry registry) {
    //        // Cho phép React kết nối với WebSocket của Spring Boot
    //        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:3000").withSockJS();
    //
    ////        registry.addEndpoint("/chat");
    ////        registry.addEndpoint("/chat").withSockJS();
    //    }
    }

