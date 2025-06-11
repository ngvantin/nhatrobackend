package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByConversationId(String conversationId);

    Optional<Conversation> findTopByUserIdOrderByUpdatedAtDesc(String userId);
}