package com.example.nhatrobackend.Entity;

import com.example.nhatrobackend.Entity.Field.PaymentType;
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
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "payment_amount", nullable = false)
    private Long paymentAmount;

    @Column(name = "payment_time", nullable = false)
    private LocalDateTime paymentTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    // Các trường cho thanh toán VNPAY
    @Column(name = "transaction_code", length = 50)
    private String transactionCode; // Mã giao dịch từ VNPAY (vnp_TxnRef)

    @Column(name = "response_code", length = 10)
    private String responseCode; // Mã phản hồi từ VNPAY (vnp_ResponseCode)

    @Column(name = "order_info", length = 255)
    private String orderInfo; // Thông tin đơn hàng truyền lên VNPAY

    // Các trường cho thanh toán hoa hồng
    @ManyToOne
    @JoinColumn(name = "deposit_id")
    private Deposit deposit; // Liên kết với đơn đặt cọc

    @Column(name = "commission_rate")
    private Double commissionRate; // Tỷ lệ hoa hồng (ví dụ: 0.1 cho 10%)

    @Column(name = "commission_amount")
    private Double commissionAmount; // Số tiền hoa hồng

    @Column(name = "description", length = 255)
    private String description; // Mô tả giao dịch

    // Bạn có thể thêm các trường khác nếu cần, ví dụ như phương thức thanh toán, trạng thái giao dịch chi tiết, v.v.
}
