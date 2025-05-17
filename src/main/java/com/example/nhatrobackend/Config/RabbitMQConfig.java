package com.example.nhatrobackend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.event";

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Exchange notificationExchange() {
        return ExchangeBuilder.directExchange(NOTIFICATION_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
/**
 * Cấu hình RabbitMQ cho hệ thống thông báo.
 * Class này định nghĩa các queue, exchange và binding để xử lý:
 * - Thông báo cá nhân (như thông báo duyệt bài, cập nhật trạng thái nhà trọ)
 * - Thông báo broadcast (như thông báo bảo trì hệ thống, cập nhật tính năng mới)
 */
// @Configuration
// public class RabbitMQConfig {

//     public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
//     public static final String NOTIFICATION_QUEUE = "notification.queue";
//     public static final String NOTIFICATION_ROUTING_KEY = "notification.event";

//     // Inject các giá trị cấu hình từ application.properties/yaml
//     @Value("${rabbitmq.queue.notification:notification-queue}")
//     private String notificationQueue;

//     @Value("${rabbitmq.exchange.notification:notification-exchange}")
//     private String notificationExchange;

//     @Value("${rabbitmq.routing.key.notification:notification-routing-key}")
//     private String notificationRoutingKey;

//     @Value("${rabbitmq.exchange.broadcast:broadcast-exchange}")
//     private String broadcastExchange;

//     @Value("${rabbitmq.queue.broadcast:broadcast-queue}")
//     private String broadcastQueue;

//     /**
//      * Tạo queue cho thông báo cá nhân
//      * - Durable: queue sẽ tồn tại sau khi restart RabbitMQ
//      * - TTL: thông báo sẽ tự động xóa sau 7 ngày
//      * Sử dụng cho: thông báo duyệt bài, cập nhật trạng thái nhà trọ, phản hồi người dùng
//      */
//     @Bean
//     public Queue notificationQueue() {
//         return QueueBuilder
//                 .durable(NOTIFICATION_QUEUE)
//                 .build();
//     }

//     /**
//      * Tạo Topic Exchange cho thông báo cá nhân
//      * Topic Exchange cho phép gửi thông báo đến người dùng cụ thể dựa trên routing key
//      * Ví dụ: có thể gửi thông báo đến user.123, user.456
//      */
//     @Bean
//     public Exchange notificationExchange() {
//         return ExchangeBuilder
//                 .directExchange(NOTIFICATION_EXCHANGE)
//                 .durable(true)
//                 .build();
//     }

//     /**
//      * Tạo binding giữa notification queue và exchange
//      * Binding này xác định cách message được route từ exchange đến queue
//      * sử dụng routing key đã cấu hình
//      */
//     @Bean
//     public Binding notificationBinding() {
//         return BindingBuilder
//                 .bind(notificationQueue())
//                 .to(notificationExchange())
//                 .with(NOTIFICATION_ROUTING_KEY)
//                 .noargs();
//     }

//     /**
//      * Tạo queue cho thông báo broadcast (gửi đến tất cả users)
//      * - Durable: queue tồn tại sau khi restart
//      * - TTL: thông báo tự động xóa sau 3 ngày
//      * Sử dụng cho: thông báo bảo trì, cập nhật tính năng, thông báo chung
//      */
//     @Bean
//     public Queue broadcastQueue() {
//         return QueueBuilder.durable(broadcastQueue)
//                 .withArgument("x-message-ttl", 259200000) // 3 ngày
//                 .build();
//     }

//     /**
//      * Tạo Fanout Exchange cho broadcast
//      * Fanout Exchange sẽ gửi message đến tất cả queue được bind với nó
//      * Không cần routing key vì gửi đến tất cả queue
//      */
//     @Bean
//     public FanoutExchange broadcastExchange() {
//         return new FanoutExchange(broadcastExchange);
//     }

//     /**
//      * Tạo binding giữa broadcast queue và fanout exchange
//      * Tất cả message gửi đến fanout exchange sẽ được forward đến broadcast queue
//      */
//     @Bean
//     public Binding broadcastBinding() {
//         return BindingBuilder
//                 .bind(broadcastQueue())
//                 .to(broadcastExchange());
//     }

//     /**
//      * Cấu hình Message Converter để chuyển đổi giữa Java objects và JSON messages
//      * Cho phép gửi và nhận các object phức tạp qua RabbitMQ
//      * Ví dụ: có thể gửi trực tiếp NotificationDTO, UserDTO, ...
//      */
//     @Bean
//     public MessageConverter jsonMessageConverter() {
//         return new Jackson2JsonMessageConverter();
//     }

//     /**
//      * Cấu hình RabbitTemplate để tương tác với RabbitMQ
//      * - Sử dụng JSON converter để serialize/deserialize messages
//      * - Template này được inject vào các service để gửi message
//      * Ví dụ: notificationService.send(new NotificationDTO(...))
//      */
//     @Bean
//     public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//         RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//         rabbitTemplate.setMessageConverter(jsonMessageConverter());
//         return rabbitTemplate;
//     }
// }

