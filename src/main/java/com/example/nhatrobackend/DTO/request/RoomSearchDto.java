package com.example.nhatrobackend.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomSearchDto {
    private Double minPrice;
    private Double maxPrice;
    private Double minArea;
    private Double maxArea;
    private String furnitureStatus;
    private String city;
    private String district;
    private String ward;
    private String keyword;
    private Integer pageNumber;
    private Integer pageSize;
} 