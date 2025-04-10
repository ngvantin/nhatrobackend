package com.example.nhatrobackend.DTO.response;

import com.example.nhatrobackend.Entity.Field.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGroupResponseDTO {
    private Long id;
    private Long recipientId;
    private MessageStatus messageStatus;
    private String fullName;
    private String profilePicture;
}
