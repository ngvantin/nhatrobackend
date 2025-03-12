package com.example.nhatrobackend.DTO.response;

import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SearchInforResponse {
    private Double minPrice;
    private Double maxPrice;
    private Double minArea;
    private Double maxArea;
    private FurnitureStatus furnitureStatus;
    private String city;
    private String district;
    private String ward;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
