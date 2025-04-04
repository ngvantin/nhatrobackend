package com.example.nhatrobackend.DTO.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageCreateRequest {
    private Long senderId;
    private Long recipientId;
    private String content;
    private LocalDateTime timestamp;
}
