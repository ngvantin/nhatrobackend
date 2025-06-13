package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Integer> {
    List<Deposit> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);
    List<Deposit> findByPost_User_UserIdOrderByCreatedAtDesc(Integer userId);
} 