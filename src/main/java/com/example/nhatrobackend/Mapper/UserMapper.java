package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.OtpVerificationDTO;
import com.example.nhatrobackend.DTO.RegisterRequestDTO;
import com.example.nhatrobackend.DTO.UserDetailDTO;
import com.example.nhatrobackend.DTO.UserProfileDTO;
import com.example.nhatrobackend.Entity.Account;
import com.example.nhatrobackend.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {
    UserDetailDTO toUserDetailDTO(User user);
    @Mapping(target = "userId", ignore = true) // Bỏ qua id vì nó được tự động sinh
    @Mapping(target = "createdAt", ignore = true) // Trường được set thủ công
    User toEntity(OtpVerificationDTO dto);

    UserProfileDTO toUserProfileDTO(User user);
}
