package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import com.example.nhatrobackend.Entity.Field.PostStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class PostRequestDTO {
    private List<String> postImages;
    private String title;
    private String description;
    private double depositAmount;
    private String videoUrl;
    private Double price; // Giá thuê của phòng
    private Double area; // Diện tích phòng (m2)
    private FurnitureStatus furnitureStatus; // Tình trạng nội thất của phòng
    private Integer numberOfRooms; // Số lượng phòng trong một nhà trọ
    private Double electricityPrice; // Giá điện cho phòng
    private Double waterPrice; // Giá nước cho phòng
    private String city; // Tỉnh/Thành phố
    private String district; // Quận/Huyện
    private String ward; // Phường/Xã
    private String street; // Tên đường
    private String houseNumber; // Số nhà
    private String licensePcccUrl; // Đường dẫn đến giấy chứng nhận PCCC
    private String licenseBusinessUrl; // Đường dẫn đến giấy phép kinh doanh
    private Boolean allowDeposit; // Cho phép đặt cọc hay không
}
