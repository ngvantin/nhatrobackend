package com.example.nhatrobackend.Entity;


import com.example.nhatrobackend.Entity.Field.LandlordStatus;
import com.example.nhatrobackend.Entity.Field.UserStatus;
import com.example.nhatrobackend.Entity.Field.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_uuid", nullable = false, unique = true, length = 36)
    private String userUuid = UUID.randomUUID().toString();

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "profile_picture", length = 255)
    private String profilePicture;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING) // Lưu giá trị enum dưới dạng chuỗi vào cột trong database.
//    @JdbcTypeCode(SqlTypes.NAMED_ENUM) // chỉ hoạt động với PostgreSQL vì nó hỗ trợ ENUM và sẽ lưu kiểu ENUM dưới db, còn loại khác như mysql sẽ lưu varchar
    @Column(name = "type")
    private UserType type;

    @Enumerated(EnumType.STRING)
//    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "address", length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_landlord_activated", nullable = false)
    private LandlordStatus isLandlordActivated;

    @Column(name = "front_cccd_url", length = 255)
    private String frontCccdUrl;

    @Column(name = "back_cccd_url", length = 100)
    private String backCccdUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Quan hệ 1-N với Post
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Post> post;

//    // Thiết lập quan hệ 1-1 với Account
//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Account account;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoritePost> favoritePosts ;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportPost> reportPosts;

    @OneToMany(mappedBy = "user")
    private Set<UserHasRole> roles = new HashSet<>();


//    @Override
//    public String getPassword() {
//        return null;
//    }

    @Override
    public String getUsername() {
        return this.phoneNumber;
    }
    // Trả về các vai trò và quyền được cấp cho người dùng.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    // Cho biết tài khoản của người dùng có hết hạn hay không.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Cho biết người dùng bị khóa hay không.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // : Cho biết thông tin đăng nhập của người dùng (mật khẩu) có hết hạn hay không.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Cho biết người dùng có được kích hoạt hay không.
    @Override
    public boolean isEnabled() {
        return true;
    }
}