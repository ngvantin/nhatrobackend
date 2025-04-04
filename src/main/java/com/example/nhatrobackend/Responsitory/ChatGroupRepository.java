package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroup,Long> {
    Optional<ChatGroup> findBySenderIdAndRecipientId(Long senderId, Long recipientId);

    List<ChatGroup> findAllBySenderId(Long senderId);

    Optional<ChatGroup> findBySenderIdOrRecipientId(Long senderId, Long recipientId);
}
