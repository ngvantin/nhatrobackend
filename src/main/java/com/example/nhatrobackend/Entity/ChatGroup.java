package com.example.nhatrobackend.Entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name = "tbl_chat_group")
public class ChatGroup {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_group_id") // Hoặc @Column(name = "conversation_id")
    private Long chatGroupId;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Bạn có thể thêm các trường khác nếu cần, ví dụ: last_message_at,...

    // Để dễ dàng so sánh và tìm kiếm các cuộc trò chuyện giữa hai người,
    // bạn có thể override phương thức equals và hashCode dựa trên sender và recipient.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatGroup chatGroup = (ChatGroup) o;
        // Đảm bảo thứ tự sender và recipient không quan trọng khi so sánh
        return (sender.equals(chatGroup.sender) && recipient.equals(chatGroup.recipient)) ||
                (sender.equals(chatGroup.recipient) && recipient.equals(chatGroup.sender));
    }

    @Override
    public int hashCode() {
        // Đảm bảo thứ tự sender và recipient không quan trọng khi tính toán hashCode
        int hash1 = sender.hashCode();
        int hash2 = recipient.hashCode();
        return (hash1 < hash2) ? (31 * hash1 + hash2) : (31 * hash2 + hash1);
    }



//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "chat_uuid")
//    private String chatUuid; //= UUID.randomUUID().toString();
//
//    @ManyToOne
//    @JoinColumn(name = "sender_id", referencedColumnName = "userId", insertable = false, updatable = false)
//    private User sender;
//
//    @ManyToOne
//    @JoinColumn(name = "recipient_id", referencedColumnName = "userId", insertable = false, updatable = false)
//    private User recipient;
}
