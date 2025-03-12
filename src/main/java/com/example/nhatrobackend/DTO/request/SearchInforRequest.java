package com.example.nhatrobackend.DTO.request;

import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchInforRequest {
    private Double minPrice;
    private Double maxPrice;
    private Double minArea;
    private Double maxArea;
    private FurnitureStatus furnitureStatus;
    private String city;
    private String district;
    private String ward;
}
