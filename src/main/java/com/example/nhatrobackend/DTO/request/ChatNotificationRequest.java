package com.example.nhatrobackend.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotificationRequest {
    private Long id;
    private Integer senderId;
    private Integer recipientId;
    private String content;
}

