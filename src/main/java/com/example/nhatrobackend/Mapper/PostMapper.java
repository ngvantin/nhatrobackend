package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.PostDetailResponseDTO;
import com.example.nhatrobackend.DTO.PostRequestDTO;
import com.example.nhatrobackend.DTO.PostResponseDTO;
import com.example.nhatrobackend.DTO.RoomDTO;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.PostImage;
import com.example.nhatrobackend.Entity.Room;
import com.example.nhatrobackend.Entity.User;
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


}
