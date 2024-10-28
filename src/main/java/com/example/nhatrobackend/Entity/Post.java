package com.example.nhatrobackend.Entity;

import com.example.nhatrobackend.Entity.Field.PostStatus;
import jakarta.persistence.*;
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
@Entity
@Table(name = "post") // Tên bảng trong cơ sở dữ liệu
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng
    private int postId;

    @Column(nullable = false) // Không cho phép null
    private String title;

    @Column(nullable = false) // Không cho phép null
    private String description;

    @Column(name = "deposit_amount", nullable = false) // Tên cột trong DB
    private double depositAmount;

    @Column(name = "video_url") // Tên cột trong DB
    private String videoUrl;

    @Enumerated(EnumType.STRING) // Enum lưu trữ dưới dạng chuỗi
    @Column(nullable = false)
    private PostStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // Thiết lập quan hệ 1-1 với Rooms
    @OneToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // Quan hệ 1-N với PostImage
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages;

}
