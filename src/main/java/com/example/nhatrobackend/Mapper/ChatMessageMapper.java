package com.example.nhatrobackend.Mapper;



import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.response.ChatMessageResponse;
import com.example.nhatrobackend.Entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.ZoneId;
import java.util.Date;

@Mapper(config = MapStructConfig.class)
public interface ChatMessageMapper {

    @Mapping(source = "messageId", target = "messageId")
    @Mapping(source = "sender.userId", target = "senderId")
    @Mapping(source = "recipient.userId", target = "recipientId")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "isRead", target = "isRead")
    ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage);


}
