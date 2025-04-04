package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.request.MessageCreateRequest;
import com.example.nhatrobackend.Entity.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatMessageService {
    ChatMessage save(MessageCreateRequest messageCreateRequest);
    List<ChatMessage> findChatMessages(Long senderId, Long recipientId);
}
