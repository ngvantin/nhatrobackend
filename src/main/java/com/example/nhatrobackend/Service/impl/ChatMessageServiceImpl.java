package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.DTO.request.MessageCreateRequest;
import com.example.nhatrobackend.Entity.ChatMessage;
import com.example.nhatrobackend.Responsitory.ChatMessageRepository;
import com.example.nhatrobackend.Service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatGroupServiceIml chatGroupServiceIml;

    @Override
    public ChatMessage save(MessageCreateRequest messageCreateRequest) {

        ChatMessage chatMessage = ChatMessage.builder().content(messageCreateRequest.getContent()).recipientId(messageCreateRequest.getRecipientId()).timestamp(messageCreateRequest.getTimestamp()).senderId(messageCreateRequest.getSenderId()).build();

        var chatUuid = chatGroupServiceIml.getChatRoomId(chatMessage.getSenderId(),chatMessage.getRecipientId(),true)
                .orElseThrow();
        chatMessage.setChatUuid(chatUuid);
        chatMessageRepository.save(chatMessage);
        return chatMessage;

    }

    @Override
    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId) {
        Optional<String> chatUuid = chatGroupServiceIml.getChatRoomId(senderId, recipientId, false);
        //Phương thức map chỉ được thực thi nếu chatUuid chứa một giá trị (tức là không rỗng).
        // chatMessageRepository::findByChatUuid là một method reference, tương đương với chatUuidValue -> chatMessageRepository.findByChatUuid(chatUuidValue).
        return chatUuid.map(chatMessageRepository::findByChatUuid).orElse(new ArrayList<>());
    }
}
