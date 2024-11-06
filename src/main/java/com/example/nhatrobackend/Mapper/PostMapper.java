package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.PostDetailResponseDTO;
import com.example.nhatrobackend.DTO.PostResponseDTO;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.PostImage;
import com.example.nhatrobackend.Entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(config = MapStructConfig.class)
public interface PostMapper {
    // Ánh xạ từ Post sang PostResponseDTO
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

    // Phương thức ánh xạ danh sách URL từ PostImage
    @Named("mapImagesToUrls")
    default List<String> mapImagesToUrls(List<PostImage> postImages) {
        return postImages.stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());
    }
}

//
//@Mapper(config = MapStructConfig.class, uses = {RoomMapper.class, PostImageMapper.class})
//public interface PostMapper extends EntityMapper<PostResponseDTO, Post> {
//
//    // Ánh xạ Post sang PostResponseDTO
//    @Mapping(source = "room.price", target = "price")
//    @Mapping(source = "room.area", target = "area")
//    @Mapping(source = "room.city", target = "city")
//    @Mapping(source = "room.district", target = "district")
//    @Mapping(source = "room.ward", target = "ward")
//    @Mapping(source = "postImages", target = "postImages", qualifiedByName = "mapPostImagesToUrls")
//    PostResponseDTO toDto(Post post);
//
//    // Phương thức ánh xạ danh sách PostImage sang danh sách URL
//    @Named("mapPostImagesToUrls")
//    default List<String> mapPostImagesToUrls(List<PostImage> images) {
//        if (images == null) return null;
//        return images.stream()
//                .map(PostImage::getImageUrl)  // Lấy URL từ mỗi PostImage
//                .collect(Collectors.toList());
//    }
//}


//@Mapper(config = MapStructConfig.class)
//public interface PostMapper extends EntityMapper<PostResponseDTO, Post> {
//
//    // Sử dụng phương thức ánh xạ tùy chỉnh cho Room
//    @Mapping(source = "post.room", target = ".", qualifiedByName = "mapRoom")
//    @Mapping(source = "postImages", target = "postImages", qualifiedByName = "mapPostImages")
//    PostResponseDTO toDto(Post post);
//
//    // Phương thức ánh xạ danh sách URL từ danh sách PostImage
//    @Named("mapPostImages")
//    default List<String> mapPostImages(List<PostImage> postImages) {
//        if (postImages == null) {
//            return new ArrayList<>();
//        }
//        return postImages.stream()
//                .map(PostImage::getImageUrl)
//                .collect(Collectors.toList());
//    }
//
//    // Ánh xạ tùy chỉnh cho các trường Room
//    @Named("mapRoom")
//    default void mapRoom(@MappingTarget PostResponseDTO postResponseDTO, Room room) {
//        if (room != null) {
//            postResponseDTO.setPrice(room.getPrice());
//            postResponseDTO.setArea(room.getArea());
//            postResponseDTO.setCity(room.getCity());
//            postResponseDTO.setDistrict(room.getDistrict());
//            postResponseDTO.setWard(room.getWard());
//        }
//    }
//}
