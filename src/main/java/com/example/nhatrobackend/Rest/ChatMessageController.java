package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.ReportPostAdminDTO;
import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.DTO.request.MessageCreateRequest;
import com.example.nhatrobackend.DTO.response.SimilarPostResponse;
import com.example.nhatrobackend.Entity.ChatMessage;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final AuthenticationFacade authenticationFacade;


    @GetMapping("/api/messages/{recipientId}")
    public ResponseEntity<ResponseWrapper<List<ChatMessage>>> findChatMessages(@PathVariable Long recipientId) {
        Long senderId = Long.valueOf(authenticationFacade.getCurrentUserId());

        List<ChatMessage> messageList = chatMessageService.findChatMessages(senderId, recipientId);
        return ResponseEntity.ok(ResponseWrapper.<List<ChatMessage>>builder()
                .status("success")
                .message("Các đoạn chat của 2 người dùng.")
                .data(messageList)
                .build());
    }
}
