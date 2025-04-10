package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.response.ChatGroupResponseDTO;
import com.example.nhatrobackend.Entity.ChatGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface ChatGroupMapper {

    @Mapping(source = "chatGroupId", target = "id") // Ánh xạ chatGroupId sang id của DTO
    @Mapping(target = "recipientId", ignore = true) // Sẽ được set thủ công trong service
    @Mapping(target = "messageStatus", ignore = true) // Sẽ được lấy từ User và set thủ công (nếu cần)
    @Mapping(target = "fullName", ignore = true) // Sẽ được lấy từ User và set thủ công
    @Mapping(target = "profilePicture", ignore = true) // Sẽ được lấy từ User và set thủ công
    ChatGroupResponseDTO toChatGroupResponseDTO(ChatGroup chatGroup);
}