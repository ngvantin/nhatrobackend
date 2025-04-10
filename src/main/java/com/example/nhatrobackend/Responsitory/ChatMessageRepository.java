package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.ChatGroup;
import com.example.nhatrobackend.Entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
//    Optional<ChatGroup> findByChatGroup(ChatGroup chatGroup);
//    List<ChatMessage> findByChatUuid(String chatUuid);
    List<ChatMessage> findByChatGroup(ChatGroup chatGroup);

}

