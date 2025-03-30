package com.example.nhatrobackend.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "follower")
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follower_id")
    private Long followerId;

    @ManyToOne
    @JoinColumn(name = "following_user_id", nullable = false)
    private User followingUser; // Người mà người dùng đang theo dõi

    @ManyToOne
    @JoinColumn(name = "followed_user_id", nullable = false)
    private User followedUser; // Người dùng được theo dõi

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Equals và HashCode (quan trọng để sử dụng trong Set)

    // định nghĩa khi nào hai đối tượng Follower được coi là giống nhau về mặt nội dung.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follower follower = (Follower) o;
        return followingUser.getUserId().equals(follower.followingUser.getUserId()) && followedUser.getUserId().equals(follower.followedUser.getUserId());
    }

    //  cung cấp một giá trị số nguyên đại diện cho đối tượng
    @Override
    public int hashCode() {
        int result = followingUser.getUserId().hashCode();
        result = 31 * result + followedUser.getUserId().hashCode();
        return result;
    }
}