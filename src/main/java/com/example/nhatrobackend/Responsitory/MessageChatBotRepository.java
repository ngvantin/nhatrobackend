package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.MessageChatBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageChatBotRepository extends JpaRepository<MessageChatBot, Long> {
    @Query("SELECT m FROM MessageChatBot m WHERE m.conversation.conversationId = :conversationId ORDER BY m.createdAt ASC")
    List<MessageChatBot> findByConversationIdOrderByCreatedAtAsc(@Param("conversationId") String conversationId);
}