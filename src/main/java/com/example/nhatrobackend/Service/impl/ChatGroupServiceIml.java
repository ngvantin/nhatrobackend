package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.DTO.response.ChatGroupResponseDTO;
import com.example.nhatrobackend.Entity.ChatGroup;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Mapper.ChatGroupMapper;
import com.example.nhatrobackend.Responsitory.ChatGroupRepository;
import com.example.nhatrobackend.Service.ChatGroupService;
import com.example.nhatrobackend.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ChatGroupServiceIml implements ChatGroupService {

    private final ChatGroupRepository chatGroupRepository;
    private final ChatGroupMapper chatGroupMapper;
    private final UserService userService;

    @Override
    public Optional<String> getChatRoomId(Long senderId, Long recipientId, boolean createNewRoomIfNotExists) {
        // Tìm kiếm ChatGroup theo cả hai hướng (sender-recipient hoặc recipient-sender)
        Optional<ChatGroup> existingChatGroup = chatGroupRepository.findBySenderUserIdAndRecipientUserId(senderId, recipientId)
                .or(() -> chatGroupRepository.findBySenderUserIdAndRecipientUserId(recipientId, senderId));

        if (existingChatGroup.isPresent()) {
            return existingChatGroup.map(ChatGroup::getChatGroupId).map(String::valueOf);
        } else {
            if (createNewRoomIfNotExists) {
                // Tìm User objects từ senderId và recipientId
                User sender = userService.findByUserId(Math.toIntExact(senderId));
                User recipient = userService.findByUserId(Math.toIntExact(recipientId));

                // Tạo một ChatGroup mới
                ChatGroup newChatGroup = ChatGroup.builder()
                        .sender(sender)
                        .recipient(recipient)
                        .createdAt(LocalDateTime.now())
                        .build();

                // Lưu ChatGroup mới và trả về ID của nó
                ChatGroup savedChatGroup = chatGroupRepository.save(newChatGroup);
                return Optional.of(String.valueOf(savedChatGroup.getChatGroupId()));
            } else {
                return Optional.empty();
            }
        }
    }
//
//    @Override
//    public Optional<String> getChatRoomId(Long senderId, Long recipientId, boolean createNewRoomIfNotExists) {
//        // Tìm kiếm ChatGroup theo cả hai hướng (sender-recipient hoặc recipient-sender)
//        Optional<ChatGroup> existingChatGroup = chatGroupRepository.findBySenderUserIdAndRecipientUserId(senderId, recipientId)
//                .or(() -> chatGroupRepository.findBySenderUserIdAndRecipientUserId(recipientId, senderId));
//
//        return existingChatGroup.map(ChatGroup::getChatGroupId).map(String::valueOf) // Sử dụng chatGroupId làm ID
//                .or(() -> {
//                    if (createNewRoomIfNotExists) {
//                        // Tìm User objects từ senderId và recipientId
//                        User sender = userService.findByUserId(Math.toIntExact(senderId));
//                        User recipient = userService.findByUserId(Math.toIntExact(recipientId));
//
//                        // Tạo một ChatGroup mới
//                        ChatGroup newChatGroup = ChatGroup.builder()
//                                .sender(sender)
//                                .recipient(recipient)
//                                .createdAt(LocalDateTime.now())
//                                .build();
//
//                        // Lưu ChatGroup mới và trả về ID của nó
//                        ChatGroup savedChatGroup = chatGroupRepository.save(newChatGroup);
//                        return Optional.of(String.valueOf(savedChatGroup.getChatGroupId()));
//                    }
//                    return Optional.empty();
//                });
//    }

    @Override
    public Optional<ChatGroup> getChatRoom(Long senderId, Long recipientId, boolean createNewRoomIfNotExists) {
        Optional<ChatGroup> existingChatGroup = chatGroupRepository.findBySenderUserIdAndRecipientUserId(senderId, recipientId)
                .or(() -> chatGroupRepository.findBySenderUserIdAndRecipientUserId(recipientId, senderId));

        if (existingChatGroup.isPresent()) {
            return existingChatGroup;
        } else if (createNewRoomIfNotExists) {
            return Optional.of(createNewChatGroup(senderId, recipientId));
        } else {
            return Optional.empty();
        }
    }

    public ChatGroup createNewChatGroup(Long senderId, Long recipientId) {
        User sender = userService.findByUserId(Math.toIntExact(senderId));
        User recipient = userService.findByUserId(Math.toIntExact(recipientId));

        ChatGroup newChatGroup = ChatGroup.builder()
                .sender(sender)
                .recipient(recipient)
                .createdAt(LocalDateTime.now())
                .build();

        return chatGroupRepository.save(newChatGroup);
    }


    // tìm chatUuid nếu không thấy sẽ tạo một chatUuid
//    @Override
//    public Optional<String> getChatRoomId(Long senderId, Long recipientId, boolean createNewRoomIfNotExists) {
//        return chatGroupRepository
//                .findBySenderIdAndRecipientId(senderId, recipientId)
//                .map(ChatGroup::getChatUuid)
//                .or(() -> {
//                    if(createNewRoomIfNotExists) {
//                        var chatId = createChatId(senderId, recipientId);
//                        return Optional.of(chatId);
//                    }
//
//                    return  Optional.empty();
//                });
//    }

//    @Override
//    public String createChatId(Long senderId, Long recipientId) {
//        var chatId = String.format("%s_%s", senderId, recipientId);
//
//        ChatGroup senderRecipient = ChatGroup
//                .builder()
//                .chatUuid(chatId)
//                .senderId(senderId)
//                .recipientId(recipientId)
//                .build();
//
//        ChatGroup recipientSender = ChatGroup
//                .builder()
//                .chatUuid(chatId)
//                .senderId(recipientId)
//                .recipientId(senderId)
//                .build();
//
//        chatGroupRepository.save(senderRecipient);
//        chatGroupRepository.save(recipientSender);
//
//        return chatId;
//
//    }

//    @Override
//    public List<ChatGroup> findGroupMessages(Long userId) {
//        return chatGroupRepository.findAllBySenderId(userId);
//    }

//    @Override
//    public List<ChatGroupResponseDTO> findGroupMessages(Long userId) {
////        List<ChatGroup> chatGroups = chatGroupRepository.findAllBySenderId(userId);
//        List<ChatGroup> chatGroups = chatGroupRepository.findAllBySender_UserId(userId);
//
//        return chatGroups.stream()
//                .map(chatGroup -> {
//                    User recipient = chatGroup.getSender();
//                    ChatGroupResponseDTO dto = chatGroupMapper.toChatGroupResponseDTO(chatGroup);
//                    dto.setRecipientId(recipient.getUserId().longValue());
//                    dto.setFullName(recipient.getFullName());
//                    dto.setProfilePicture(recipient.getProfilePicture());
//                    dto.setMessageStatus(recipient.getMessageStatus());
//                    return dto;
//                })
//                .collect(Collectors.toList());
//    }
@Override
public List<ChatGroupResponseDTO> findGroupMessages(Long userId) {
    // Lấy danh sách các ChatGroup mà userId là người gửi hoặc người nhận
    List<ChatGroup> chatGroups = chatGroupRepository.findBySenderUserIdOrRecipientUserId(userId, userId);

    // Sử dụng Set để lưu trữ các User đã từng nhắn tin (tránh trùng lặp)
    Set<Long> interactedUserIds = new HashSet<>();

    // Duyệt qua danh sách ChatGroup và thu thập ID của người dùng khác
    for (ChatGroup chatGroup : chatGroups) {
        if (chatGroup.getSender().getUserId().longValue() == userId) {
            interactedUserIds.add(chatGroup.getRecipient().getUserId().longValue());
        } else if (chatGroup.getRecipient().getUserId().longValue() == userId) {
            interactedUserIds.add(chatGroup.getSender().getUserId().longValue());
        }
    }

    // Loại bỏ chính userId khỏi danh sách tương tác (nếu có)
    interactedUserIds.remove(userId);

    // Lấy thông tin chi tiết của các User đã tương tác và map sang DTO
    List<ChatGroupResponseDTO> responseList = new ArrayList<>();
    for (Long interactedUserId : interactedUserIds) {
        User user = userService.findByUserId(Math.toIntExact(interactedUserId));
        if (user != null) {
            responseList.add(ChatGroupResponseDTO.builder()
                    .id(interactedUserId) // Sử dụng userId của người đã tương tác làm ID trong DTO
                    .recipientId(user.getUserId().longValue())
                    .fullName(user.getFullName())
                    .profilePicture(user.getProfilePicture())
                    // Bạn có thể cần logic phức tạp hơn để lấy MessageStatus
                    // Hiện tại, chúng ta có thể để mặc định hoặc lấy từ User entity
                    .messageStatus(user.getMessageStatus())
                    .build());
        }
    }

    return responseList;
}
}

