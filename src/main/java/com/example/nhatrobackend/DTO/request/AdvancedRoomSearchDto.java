package com.example.nhatrobackend.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedRoomSearchDto {
    private Double minPrice;
    private Double maxPrice;
    private Double minArea;
    private Double maxArea;
    private String city;
    private String district;
    private String ward;
    private String referenceAddress; // Địa chỉ tham chiếu
    private Double maxDistance; // Khoảng cách tối đa (km)
    private Integer pageNumber;
    private Integer pageSize;
} 