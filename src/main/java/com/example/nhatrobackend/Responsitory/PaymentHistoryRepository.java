package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findByUser_UserId(Integer userId); // Đổi tên method
}
