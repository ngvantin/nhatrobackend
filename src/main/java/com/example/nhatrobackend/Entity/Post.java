package com.example.nhatrobackend.Entity;

import com.example.nhatrobackend.Entity.Field.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import org.mapstruct.control.MappingControl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@ToString(exclude )
@Entity
@Table(name = "post") // Tên bảng trong cơ sở dữ liệu
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng
    private int postId;
    @Column(name = "post_uuid", nullable = false, unique = true, length = 36)
    private String postUuid = UUID.randomUUID().toString();

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

    @Column(name = "allow_deposit", nullable = false)
    private boolean allowDeposit = true;

    // Thiết lập quan hệ 1-1 với Rooms
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
      // Quan hệ 1-N với PostImage
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages;

    // Quan hệ 1-N với User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoritePost> favoritePosts ;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportPost> reportPosts;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deposit> deposits;

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", postUuid='" + postUuid + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", depositAmount=" + depositAmount +
                ", videoUrl='" + videoUrl + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
//                ", room=" + room +
//                ", postImages=" + postImages +
//                ", user=" + user +
//                ", favoritePosts=" + favoritePosts +
                '}';
    }
}
