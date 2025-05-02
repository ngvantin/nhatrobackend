package com.example.nhatrobackend.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment_history")
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "payment_amount", nullable = false)
    private Long paymentAmount;

    @Column(name = "payment_time", nullable = true)
    private LocalDateTime paymentTime = LocalDateTime.now();

    @Column(name = "transaction_code", length = 50)
    private String transactionCode; // Mã giao dịch từ VNPAY (vnp_TxnRef)

    @Column(name = "response_code", length = 10)
    private String responseCode; // Mã phản hồi từ VNPAY (vnp_ResponseCode)

    @Column(name = "order_info", length = 255)
    private String orderInfo; // Thông tin đơn hàng bạn truyền lên VNPAY

    // Bạn có thể thêm các trường khác nếu cần, ví dụ như phương thức thanh toán, trạng thái giao dịch chi tiết, v.v.
}
