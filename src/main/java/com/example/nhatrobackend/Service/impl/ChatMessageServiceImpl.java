package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.DTO.request.MessageCreateRequest;
import com.example.nhatrobackend.DTO.response.ChatMessageResponse;
import com.example.nhatrobackend.Entity.ChatGroup;
import com.example.nhatrobackend.Entity.ChatMessage;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Mapper.ChatMessageMapper;
import com.example.nhatrobackend.Responsitory.ChatMessageRepository;
import com.example.nhatrobackend.Service.ChatGroupService;
import com.example.nhatrobackend.Service.ChatMessageService;
import com.example.nhatrobackend.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatGroupService chatGroupService;
    private final UserService userService;
    private final ChatMessageMapper chatMessageMapper;


//    @Override
//    public ChatMessage save(MessageCreateRequest messageCreateRequest) {
//
//        ChatMessage chatMessage = ChatMessage.builder().content(messageCreateRequest.getContent()).recipientId(messageCreateRequest.getRecipientId()).timestamp(messageCreateRequest.getTimestamp()).senderId(messageCreateRequest.getSenderId()).build();
//
//        var chatUuid = chatGroupServiceIml.getChatRoomId(chatMessage.getSenderId(),chatMessage.getRecipientId(),true)
//                .orElseThrow();
//        chatMessage.setChatUuid(chatUuid);
//        chatMessageRepository.save(chatMessage);
//        return chatMessage;
//    }

    @Override
    public ChatMessage save(MessageCreateRequest messageCreateRequest) {
        Long senderId = messageCreateRequest.getSenderId();
        Long recipientId = messageCreateRequest.getRecipientId();

        // Tìm hoặc tạo ChatGroup thông qua ChatGroupService và lấy trực tiếp đối tượng ChatGroup
        ChatGroup chatGroup = chatGroupService.getChatRoom(senderId, recipientId, true)
                .orElseThrow(() -> new RuntimeException("Failed to find or create ChatGroup"));

        // Tìm User objects từ senderId và recipientId
        User sender = userService.findByUserId(Math.toIntExact(senderId));
        User recipient = userService.findByUserId(Math.toIntExact(recipientId));

        ChatMessage chatMessage = ChatMessage.builder()
                .content(messageCreateRequest.getContent())
                .sender(sender)
                .recipient(recipient)
                .timestamp(messageCreateRequest.getTimestamp())
                .chatGroup(chatGroup)
                .isRead(false) // Mặc định là chưa đọc
                .build();

        return chatMessageRepository.save(chatMessage);
    }

//    @Override
//    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId) {
//        Optional<String> chatUuid = chatGroupService.getChatRoomId(senderId, recipientId, false);
//        //Phương thức map chỉ được thực thi nếu chatUuid chứa một giá trị (tức là không rỗng).
//        // chatMessageRepository::findByChatUuid là một method reference, tương đương với chatUuidValue -> chatMessageRepository.findByChatUuid(chatUuidValue).
//        return chatUuid.map(chatMessageRepository::findByChatUuid).orElse(new ArrayList<>());
//
//    }

    @Override
    public List<ChatMessageResponse> findChatMessages(Long senderId, Long recipientId) {
        // Lấy ChatGroup giữa hai người dùng (nếu tồn tại)
        Optional<ChatGroup> chatGroupOptional = chatGroupService.getChatRoom(senderId, recipientId, false);

        return chatGroupOptional.map(chatGroup -> {
            // Tìm tất cả ChatMessage thuộc về ChatGroup này và ánh xạ sang ChatMessageResponse
            return chatMessageRepository.findByChatGroup(chatGroup).stream()
                    .map(chatMessageMapper::toChatMessageResponse)
                    .collect(Collectors.toList());
        }).orElse(new ArrayList<>());
    }

}
