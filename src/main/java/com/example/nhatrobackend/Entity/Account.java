//package com.example.nhatrobackend.Entity;
//import com.example.nhatrobackend.Entity.Field.UserStatus;
//import com.example.nhatrobackend.Entity.Field.UserType;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//
//import lombok.Data;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name = "account")
//public class Account {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "account_id")
//    private Integer accountId;
//
//    @Column(name = "phone_number", nullable = false, unique = true, length = 15)
//    private String phoneNumber;
//
//    @Column(name = "password", nullable = false, length = 255)
//    private String password;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "role", nullable = false)
//    private UserType userType;
////    @Enumerated(EnumType.STRING)
////    @Column(name = "role", nullable = false)
////    private String role;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    private UserStatus status;
//
////    // Thiết lập quan hệ 1-1 với User
////    @OneToOne
////    @JoinColumn(name = "user_id", nullable = false)
////    private User user;
//}
