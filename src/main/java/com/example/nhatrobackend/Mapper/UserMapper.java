package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.DTO.response.UserLandlordResponse;
import com.example.nhatrobackend.DTO.response.UserProfileDTO;
import com.example.nhatrobackend.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {
    @Mapping(target = "isOnline", source = "messageStatus")
    UserDetailDTO toUserDetailDTO(User user);
    @Mapping(target = "userId", ignore = true) // Bỏ qua id vì nó được tự động sinh
    @Mapping(target = "createdAt", ignore = true) // Trường được set thủ công
    User toEntity(OtpVerificationDTO dto);

    @Mapping(target = "isOnline", source = "messageStatus")
    UserProfileDTO toUserProfileDTO(User user);

    @Mapping(target = "frontCccdUrl", source = "frontCccdUrl")
    @Mapping(target = "backCccdUrl", source = "backCccdUrl")
    void updateLandlordDetails(LandlordRegistrationDTO dto, @MappingTarget User user);

    void updateUserFromDTO(UpdateUserDTO updateUserDTO, @MappingTarget User user);
    // Chuyển đổi từ User sang UpdateUserDTO
    UpdateUserDTO toUpdateUserDTO(User user);

    UserAdminDTO toUserAdminDTO(User user);

//    @Mapping(target = "userId", source = "userId")
//    @Mapping(target = "fullName", source = "fullName")
//    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
//    @Mapping(target = "phoneNumber", source = "phoneNumber")
//    @Mapping(target = "isLandlordActivated", source = "isLandlordActivated")
//    @Mapping(target = "frontCccdUrl", source = "frontCccdUrl")
//    @Mapping(target = "backCccdUrl", source = "backCccdUrl")
//    @Mapping(target = "address", source = "address")
//    @Mapping(target = "cccdNumber", source = "cccdNumber")
//    @Mapping(target = "gender", source = "gender")
//    @Mapping(target = "nationality", source = "nationality")
//    @Mapping(target = "hometown", source = "hometown")
//    @Mapping(target = "cccdIssueDate", source = "cccdIssueDate")
    UserDetailAdminDTO toUserDetailAdminDTO(User user);

    UserLandlordResponse toUserLandlordResponse(User user);
}
