package com.example.nhatrobackend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_image") // Đặt tên bảng
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng cho image_id
    @Column(name = "image_id")
    private int imageId;

    @Column(name = "image_url", nullable = false) // Không cho phép null
    private String imageUrl;

    @ManyToOne() // Quan hệ nhiều với một
    @JoinColumn(name = "post_id", nullable = false) // Khóa ngoại liên kết đến bảng Post
    private Post post; // Tham chiếu đến entity Post

    public PostImage(String imageUrl, Post post) {
        this.imageUrl = imageUrl;
        this.post = post;
    }
}
