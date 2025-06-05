package com.example.nhatrobackend.DTO.request;

import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaDTO {
    private Double price; // Giá thuê của phòng
    private Double area; // Diện tích phòng (m2)
    private FurnitureStatus furnitureStatus; // Tình trạng nội thất của phòng
    private String city; // Tỉnh/Thành phố
    private String district; // Quận/Huyện
    private String ward; // Phường/Xã
} 