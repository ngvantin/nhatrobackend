package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.Entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(config = MapStructConfig.class)
public interface PostMapper {
    PostImageMapper postImageMapper = Mappers.getMapper(PostImageMapper.class);
    RoomMapper roomMapper = Mappers.getMapper(RoomMapper.class);

    // Ánh xạ từ Post sang PostResponseDTO
    @Mapping(source = "post.room.electricityPrice", target = "electricityPrice")
    @Mapping(source = "post.room.waterPrice", target = "waterPrice")
    @Mapping(source = "post.room.price", target = "price")
    @Mapping(source = "post.room.area", target = "area")
    @Mapping(source = "post.room.city", target = "city")
    @Mapping(source = "post.room.district", target = "district")
    @Mapping(source = "post.room.ward", target = "ward")
     @Mapping(source = "post.postImages", target = "postImages", qualifiedByName = "mapImagesToUrls")
    PostResponseDTO toPostResponseDTO(Post post);

    // Ánh xạ từ trường của Room
    @Mapping(source = "room.price", target = "price")
    @Mapping(source = "room.area", target = "area")
    @Mapping(source = "room.city", target = "city")
    @Mapping(source = "room.district", target = "district")
    @Mapping(source = "room.ward", target = "ward")
    @Mapping(source = "room.numberOfRooms", target = "numberOfRooms")
    @Mapping(source = "room.electricityPrice", target = "electricityPrice")
    @Mapping(source = "room.waterPrice", target = "waterPrice")
    @Mapping(source = "room.street", target = "street")
    @Mapping(source = "room.houseNumber", target = "houseNumber")
    @Mapping(source = "room.licenseBusinessUrl", target = "licenseBusinessUrl")
    @Mapping(source = "room.licensePcccUrl", target = "licensePcccUrl")
    @Mapping(source = "postImages", target = "postImages", qualifiedByName = "mapImagesToUrls")
    PostDetailResponseDTO toPostDetailResponseDTO(Post post);

    @Mapping(target = "room", expression = "java(roomMapper.toRoom(dto))")
    @Mapping(target = "postImages", expression = "java(postImageMapper.toPostImage(dto.getPostImages(), post))")
       Post toPostWithImages(PostRequestDTO dto, User user);

    // Phương thức ánh xạ danh sách URL từ PostImage
    @Named("mapImagesToUrls")
    default List<String> mapImagesToUrls(List<PostImage> postImages) {
        return postImages.stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());
    }

    @Mapping(target = "postId", ignore = true)
    @Mapping(target = "postImages", ignore = true)
    void updatePostFromDTO(PostRequestDTO dto, @MappingTarget Post post);

    // Ánh xạ từ Post sang PostRequestDTO
    @Mapping(source = "post.postImages", target = "postImages", qualifiedByName = "mapImagesToUrls")
    PostRequestDTO toPostRequestDTO(Post post);

    // Cập nhật thông tin từ RoomDTO vào PostRequestDTO
    void updatePostRequestFromRoomDTO(RoomDTO roomDTO, @MappingTarget PostRequestDTO postRequestDTO);

    // Chuyển đổi từ Post sang PostAdminDTO
    @Mapping(source = "postId", target = "postId")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "user.fullName", target = "fullName") // Lấy fullName từ mối quan hệ User
    @Mapping(source = "createdAt", target = "createdAt")
    PostAdminDTO toPostAdminDTO(Post post);

    // dính lỗi khi sửa security xóa bảng account
//    // Phương thức chuyển đổi từ Post và ReportPost sang ReportPostDetailDTO
//    @Mapping(source = "post.room.price", target = "price")
//    @Mapping(source = "post.room.area", target = "area")
//    @Mapping(source = "post.room.city", target = "city")
//    @Mapping(source = "post.room.district", target = "district")
//    @Mapping(source = "post.room.ward", target = "ward")
//    @Mapping(source = "post.room.numberOfRooms", target = "numberOfRooms")
//    @Mapping(source = "post.room.electricityPrice", target = "electricityPrice")
//    @Mapping(source = "post.room.waterPrice", target = "waterPrice")
//    @Mapping(source = "post.room.street", target = "street")
//    @Mapping(source = "post.room.houseNumber", target = "houseNumber")
//    @Mapping(source = "post.room.licenseBusinessUrl", target = "licenseBusinessUrl")
//    @Mapping(source = "post.room.licensePcccUrl", target = "licensePcccUrl")
//    @Mapping(source = "post.postImages", target = "postImages", qualifiedByName = "mapImagesToUrls")
//    @Mapping(source = "reportPost.reportId", target = "reportId")
//    @Mapping(source = "reportPost.reason", target = "reason")
//    @Mapping(source = "reportPost.details", target = "details")
//    @Mapping(source = "reportPost.status", target = "status")
//    @Mapping(source = "reportPost.createdAt", target = "createdAt")
//    ReportPostDetailDTO toReportPostDetailDTO(Post post, ReportPost reportPost);

}
