package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostWithImagesDTO {
    private List<MultipartFile> images;
    private MultipartFile video;
    private String title;
    private String description;
    private double depositAmount;
    private String videoUrl;
    private Double price;
    private Double area;
    private FurnitureStatus furnitureStatus;
    private Integer numberOfRooms;
    private Double electricityPrice;
    private Double waterPrice;
    private String city;
    private String district;
    private String ward;
    private String street;
    private String houseNumber;
    private MultipartFile licensePccc;
    private MultipartFile licenseBusiness;
} 