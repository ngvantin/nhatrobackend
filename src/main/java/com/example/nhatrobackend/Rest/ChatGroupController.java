package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.DTO.response.ChatGroupResponseDTO;
import com.example.nhatrobackend.Entity.ChatGroup;
import com.example.nhatrobackend.Entity.ChatMessage;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.ChatGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groupchat")
@Slf4j
public class ChatGroupController {
    private final ChatGroupService chatGroupService;
    private final AuthenticationFacade authenticationFacade;

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<ChatGroupResponseDTO>>> findGroupMessages() {
        Long userId = Long.valueOf(authenticationFacade.getCurrentUserId());
        List<ChatGroupResponseDTO> chatGroups = chatGroupService.findGroupMessages(userId);
        return ResponseEntity.ok(ResponseWrapper.<List<ChatGroupResponseDTO>>builder()
                .status("success")
                .message("Các người dùng đã từng nhắn tin.")
                .data(chatGroups)
                .build());
    }


    @GetMapping("/room/{recipientId}")
    public ResponseEntity<ResponseWrapper<String>> getChatRoomId(
            @PathVariable Long recipientId,
            @RequestParam(value = "createIfNotExist", defaultValue = "true") boolean createIfNotExist) {
        Long senderId = Long.valueOf(authenticationFacade.getCurrentUserId());
        Optional<String> chatRoomIdOptional = chatGroupService.getChatRoomId(senderId, recipientId, createIfNotExist);

        return chatRoomIdOptional.map(chatRoomId -> ResponseEntity.ok(ResponseWrapper.<String>builder()
                        .status("success")
                        .message("Lấy ID phòng chat thành công.")
                        .data(chatRoomId)
                        .build()))
                .orElseGet(() -> ResponseEntity.ok(ResponseWrapper.<String>builder()
                        .status("success")
                        .message("Không tìm thấy phòng chat.")
                        .data(null) // Hoặc có thể trả về một giá trị khác tùy theo yêu cầu
                        .build()));
    }
}

