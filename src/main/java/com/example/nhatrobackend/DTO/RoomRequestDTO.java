package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequestDTO {
    private Double minPrice; // Giá tối thiểu thuê của phòng
    private Double maxPrice; // Giá tối đa thuê của phòng
    private Double minArea; // Diện tích phòng tối thiểu (m2)
    private Double maxArea; // Diện tích phòng tối đa (m2)
    private FurnitureStatus furnitureStatus; // Tình trạng nội thất của phòng
    private String city; // Tỉnh/Thành phố
    private String district; // Quận/Huyện
    private String ward; // Phường/Xã
}
