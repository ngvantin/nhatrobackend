package com.example.nhatrobackend.DTO.request;

import com.example.nhatrobackend.Entity.Field.RoleChat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCreateRequestDto {
    private String conversationId;
    private RoleChat role;
    private String content;
}
