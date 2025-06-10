package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.Field.LandlordStatus;
import com.example.nhatrobackend.Entity.Field.UserType;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.User;
import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserUuid(String userUuid);
    Page<User> findByIsLandlordActivatedOrderByCreatedAtDesc(LandlordStatus status, Pageable pageable);
    Optional<User> findByUserId(Integer userId);
    Page<User> findByIsLandlordActivatedNotOrderByCreatedAtDesc(LandlordStatus status, Pageable pageable);
    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    long countByType(UserType type);

    @Query("SELECT COUNT(u) FROM User u WHERE u.type IN ('LANDLORD', 'TENANT')")
    long countAllLandlordAndTenant();
}
