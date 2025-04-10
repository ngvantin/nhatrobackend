package com.example.nhatrobackend.Entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_chat_message")
public class ChatMessage {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "is_read")
    private Boolean isRead = false;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_stamp", updatable = false)
    private LocalDateTime timestamp;

     @ManyToOne
     @JoinColumn(name = "chat_group_id")
     private ChatGroup chatGroup;



//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "chat_uuid")
//    private String chatUuid; //= UUID.randomUUID().toString();
//
//    @ManyToOne
//    @JoinColumn(name = "chat_uuid", referencedColumnName = "chat_uuid", insertable = false, updatable = false)
//    private ChatGroup chatGroup;
//
//    @ManyToOne
//    @JoinColumn(name = "sender_id", referencedColumnName = "userId", insertable = false, updatable = false)
//    private User sender;
//
//    @ManyToOne
//    @JoinColumn(name = "recipient_id", referencedColumnName = "userId", insertable = false, updatable = false)
//    private User recipient;
//
//    @Column(name = "content")
//    private String content;
//
//    @Builder.Default
//    @Column(name = "is_read")
//    private Boolean isRead = false;
//
//    @Column(name = "time_stamp")
//    private LocalDateTime timestamp;
}
