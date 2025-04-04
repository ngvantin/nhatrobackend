package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.Entity.ChatGroup;
import com.example.nhatrobackend.Responsitory.ChatGroupRepository;
import com.example.nhatrobackend.Service.ChatGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatGroupServiceIml implements ChatGroupService {

    private final ChatGroupRepository chatGroupRepository;

    // tìm chatUuid nếu không thấy sẽ tạo một chatUuid
    @Override
    public Optional<String> getChatRoomId(Long senderId, Long recipientId, boolean createNewRoomIfNotExists) {
        return chatGroupRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatGroup::getChatUuid)
                .or(() -> {
                    if(createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }

                    return  Optional.empty();
                });
    }

    @Override
    public String createChatId(Long senderId, Long recipientId) {
        var chatId = String.format("%s_%s", senderId, recipientId);

        ChatGroup senderRecipient = ChatGroup
                .builder()
                .chatUuid(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatGroup recipientSender = ChatGroup
                .builder()
                .chatUuid(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();

        chatGroupRepository.save(senderRecipient);
        chatGroupRepository.save(recipientSender);

        return chatId;
    }

    @Override
    public List<ChatGroup> findGroupMessages(Long userId) {
        return chatGroupRepository.findAllBySenderId(userId);
    }
}

