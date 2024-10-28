package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailResponseDTO extends PostResponseDTO {
    private String description;
    private double depositAmount;
    private String videoUrl;
    private Integer numberOfRooms; // Số lượng phòng trong một nhà trọ
    private Double electricityPrice; // Giá điện cho phòng
    private Double waterPrice; // Giá nước cho phòng
    private String street; // Tên đường
    private String houseNumber; // Số nhà
}
//public class PostDetailResponseDTO {
//    private int postId;
//    private String title;
//    private String description;
//    private double depositAmount;
//    private String videoUrl;
//    private LocalDateTime createdAt;
//
//
//    private Double price; // Giá thuê của phòng
//    private Double area; // Diện tích phòng (m2)
//    private FurnitureStatus furnitureStatus; // Tình trạng nội thất của phòng
//    private Integer numberOfRooms; // Số lượng phòng trong một nhà trọ
//    private Double electricityPrice; // Giá điện cho phòng
//    private Double waterPrice; // Giá nước cho phòng
//    private String city; // Tỉnh/Thành phố
//    private String district; // Quận/Huyện
//    private String ward; // Phường/Xã
//    private String street; // Tên đường
//    private String houseNumber; // Số nhà
//
//    private List<String> postImages;
//}
