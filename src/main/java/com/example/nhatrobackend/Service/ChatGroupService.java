package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.Entity.ChatGroup;

import java.util.List;
import java.util.Optional;

public interface ChatGroupService {
    Optional<String> getChatRoomId(Long senderId, Long recipientId, boolean createNewRoomIfNotExists);
    String createChatId(Long senderId, Long recipientId);
    List<ChatGroup> findGroupMessages(Long userId);
}
