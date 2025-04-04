package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.Entity.ChatGroup;
import com.example.nhatrobackend.Entity.ChatMessage;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.ChatGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groupchat")
@Slf4j
public class ChatGroupController {
    private final ChatGroupService chatGroupService;
    private final AuthenticationFacade authenticationFacade;
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<ChatGroup>>> findGroupMessages() {
        Long senderId = Long.valueOf(authenticationFacade.getCurrentUserId());
        List<ChatGroup> chatGroups = chatGroupService.findGroupMessages(senderId);
        return ResponseEntity.ok(ResponseWrapper.<List<ChatGroup>>builder()
                .status("success")
                .message("Các người dùng đã từng nhắn tin.")
                .data(chatGroups)
                .build());
    }
}

