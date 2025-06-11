package com.example.nhatrobackend.DTO.request;

import com.example.nhatrobackend.Entity.Field.RoleChat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LatestConversationMessagesDto {
    private String conversationId;
    private String title;
    private List<MessageDto> messages;

    @Data
    public static class MessageDto {
        private RoleChat role;
        private String content;
        private LocalDateTime createdAt;
    }
}
