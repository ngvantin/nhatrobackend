package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserUuid(String userUuid);
}
