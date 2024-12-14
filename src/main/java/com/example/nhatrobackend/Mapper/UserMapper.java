package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.Entity.Account;
import com.example.nhatrobackend.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {
    UserDetailDTO toUserDetailDTO(User user);
    @Mapping(target = "userId", ignore = true) // Bỏ qua id vì nó được tự động sinh
    @Mapping(target = "createdAt", ignore = true) // Trường được set thủ công
    User toEntity(OtpVerificationDTO dto);

    UserProfileDTO toUserProfileDTO(User user);

    @Mapping(target = "frontCccdUrl", source = "frontCccdUrl")
    @Mapping(target = "backCccdUrl", source = "backCccdUrl")
    void updateLandlordDetails(LandlordRegistrationDTO dto, @MappingTarget User user);
}
