package com.example.nhatrobackend.DTO.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private Long messageId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private Boolean isRead;
    private LocalDateTime timestamp;

}