package com.example.nhatrobackend.Service;


import com.example.nhatrobackend.DTO.request.MessageCreateRequestDto;
import com.example.nhatrobackend.Entity.Conversation;
import com.example.nhatrobackend.Entity.MessageChatBot;
import com.example.nhatrobackend.Responsitory.ConversationRepository;
import com.example.nhatrobackend.Responsitory.MessageChatBotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatBotService {
    private final ConversationRepository conversationRepository;
    private final MessageChatBotRepository chatMessageRepository;

    @Transactional
    public String createConversation(String userId) {
        String conversationId = UUID.randomUUID().toString();

        Conversation conversation = new Conversation();
        conversation.setConversationId(conversationId);
        conversation.setUserId(userId);
        conversation.setTitle("Bus Route Search");

        conversationRepository.save(conversation);
        return conversationId;
    }

    @Transactional
    public void createMessage(MessageCreateRequestDto request) {
        Conversation conversation = conversationRepository
                .findByConversationId(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        MessageChatBot chatMessage = new MessageChatBot();
        chatMessage.setConversation(conversation);
        chatMessage.setRole(request.getRole());
        chatMessage.setContent(request.getContent());

        chatMessageRepository.save(chatMessage);
    }

    @Transactional(readOnly = true)
    public List<MessageChatBot> getMessages(String conversationId) {
        return chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }
}
