package com.example.nhatrobackend.Entity;

import com.example.nhatrobackend.Entity.Field.DepositStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "deposit")
public class Deposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_id")
    private int depositId;

    @Column(name = "deposit_uuid", nullable = false, unique = true, length = 36)
    @Builder.Default
    private String depositUuid = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người đặt cọc

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // Bài đăng được đặt cọc

    @Column(name = "amount", nullable = false)
    private Double amount; // Số tiền đặt cọc

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private DepositStatus status = DepositStatus.PENDING;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // Phương thức thanh toán

    @Column(name = "transaction_id")
    private String transactionId; // Mã giao dịch từ cổng thanh toán

    @Column(name = "hold_until", nullable = false)
    private LocalDateTime holdUntil; // Thời hạn giữ chỗ

    @Column(name = "landlord_confirmed")
    private Boolean landlordConfirmed = false;

    @Column(name = "tenant_confirmed")
    private Boolean tenantConfirmed; // Xác nhận từ người thuê

    @Column(name = "cancellation_reason")
    private String cancellationReason; // Lý do hủy (nếu có)

    @Column(name = "tenant_complaint_reason", columnDefinition = "TEXT")
    private String tenantComplaintReason;

    @Column(name = "tenant_complaint_video_url")
    private String tenantComplaintVideoUrl;

    @Column(name = "landlord_complaint_reason", columnDefinition = "TEXT")
    private String landlordComplaintReason;

    @Column(name = "landlord_complaint_video_url")
    private String landlordComplaintVideoUrl;

    @OneToMany(mappedBy = "deposit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DepositTenantComplaintImage> tenantComplaintImages;

    @OneToMany(mappedBy = "deposit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DepositLandlordComplaintImage> landlordComplaintImages;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "refund_amount")
    private Double refundAmount; // Số tiền hoàn trả (nếu có)

    @Column(name = "refund_transaction_id")
    private String refundTransactionId; // Mã giao dịch hoàn tiền

    @Column(name = "deposit_agreement_url")
    private String depositAgreementUrl; // URL của biên bản đặt cọc
} 