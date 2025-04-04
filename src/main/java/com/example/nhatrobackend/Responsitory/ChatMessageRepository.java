package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    List<ChatMessage> findByChatUuid(String chatUuid);
}

