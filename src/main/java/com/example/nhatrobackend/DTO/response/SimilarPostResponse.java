package com.example.nhatrobackend.DTO.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SimilarPostResponse {
    private String postUuid;
    private List<String> postImages;
    private String title;
    private Double price;
    private Double area;
    private String city;
    private String district;
    private String ward;
    private LocalDateTime createdAt;
}