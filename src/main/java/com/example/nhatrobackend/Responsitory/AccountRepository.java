//package com.example.nhatrobackend.Responsitory;
//
//import com.example.nhatrobackend.Entity.Account;
//import com.example.nhatrobackend.Entity.Field.UserType;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface AccountRepository extends JpaRepository<Account, Integer> {
//    Optional<Account> findByPhoneNumber(String phoneNumber);
//    boolean existsByPhoneNumber(String phoneNumber);
//    Optional<Account> findByRole(UserType userType);
//    Optional<Account> findByUser_UserId(Integer userId);
//}
// // dính lỗi khi sửa security xóa bảng account
