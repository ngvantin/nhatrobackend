package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.PostStatus;
import com.example.nhatrobackend.Entity.Field.ReportStatus;
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
public class ReportPostDetailDTO {
    private int postId;
    private String postUuid;
    private List<String> postImages;
    private String title;
    private Double price;
    private Double area;
    private String city;
    private String district;
    private String ward;
//    private LocalDateTime createdAt;
    private String description;
    private double depositAmount;
    private String videoUrl;
    private Integer numberOfRooms;
    private Double electricityPrice;
    private Double waterPrice;
    private String street;
    private String houseNumber;
    private String licensePcccUrl;
    private String licenseBusinessUrl;
//    private PostStatus status;

    private Integer reportId;
    private String reason;
    private String details;
    private ReportStatus status;
    private LocalDateTime createdAt;
}
