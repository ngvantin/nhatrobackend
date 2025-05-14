package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.ReportPostAdminDTO;
import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.DTO.request.ChatNotificationRequest;
import com.example.nhatrobackend.DTO.request.MessageCreateRequest;
import com.example.nhatrobackend.DTO.response.ChatMessageResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final AuthenticationFacade authenticationFacade;


    @MessageMapping("/chat")
    public void processMessage(@Payload MessageCreateRequest messageCreateRequest) {
        log.info("🔥 Nhận tin nhắn từ client qua WebSocket: {}", messageCreateRequest.getContent());

        ChatMessage savedMsg = chatMessageService.save(messageCreateRequest);
        // messagingTemplate: gửi tin nhắn thông qua WebSocket.  ID của người nhận tin,  Endpoint WebSocket mà người nhận đang lắng nghe,
        //  ChatNotificationRequest được sử dụng để đóng gói dữ liệu tin nhắn cần gửi đến người nhận qua WebSocket
        messagingTemplate.convertAndSendToUser(
                messageCreateRequest.getRecipientId().toString(), "/queue/messages",
                new ChatNotificationRequest(

                        savedMsg.getMessageId(),
                        savedMsg.getSender().getUserId(),
                        savedMsg.getRecipient().getUserId(),
                        savedMsg.getContent()
                )
        );
    }


    @GetMapping("/api/messages/{recipientId}")
    public ResponseEntity<ResponseWrapper<List<ChatMessageResponse>>> findChatMessages(@PathVariable Long recipientId) {
        Long senderId = Long.valueOf(authenticationFacade.getCurrentUserId());

        List<ChatMessageResponse> messageList = chatMessageService.findChatMessages(senderId, recipientId);
        return ResponseEntity.ok(ResponseWrapper.<List<ChatMessageResponse>>builder()
                .status("success")
                .message("Các đoạn chat của 2 người dùng.")
                .data(messageList)
                .build());
    }

    @PostMapping("api/chat")
    public ResponseEntity<ResponseWrapper<ChatNotificationRequest>> sendMessage(
            @RequestBody MessageCreateRequest messageCreateRequest) {
        ChatMessage savedMsg = chatMessageService.save(messageCreateRequest);
        ChatNotificationRequest chatNotificationRequest= new ChatNotificationRequest(

                savedMsg.getMessageId(),
                savedMsg.getSender().getUserId(),
                savedMsg.getRecipient().getUserId(),
                savedMsg.getContent()
        );
        return ResponseEntity.ok(ResponseWrapper.<ChatNotificationRequest>builder()
                .status("success")
                .message("Tin nhắn đã được gửi.")
                .data(chatNotificationRequest)
                .build());
    }
}
