package com.example.nhatrobackend.DTO;

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
public class PostResponseDTO {
    private int postId;
    private List<String> postImages;
    private String title;
    private Double price; // Giá thuê của phòng
    private Double area; // Diện tích phòng (m2)
    private String city; // Tỉnh/Thành phố
    private String district; // Quận/Huyện
    private String ward; // Phường/Xã
    private LocalDateTime createdAt;


}
