package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findByUser_UserId(Integer userId); // Đổi tên method

    Optional<PaymentHistory> findByPaymentIdAndUser_UserId(Long paymentId, Integer userId);

    @Query("SELECT FUNCTION('YEAR', p.paymentTime) as year, " +
           "FUNCTION('MONTH', p.paymentTime) as month, " +
           "COALESCE(SUM(p.paymentAmount), 0) as revenue " +
           "FROM PaymentHistory p " +
           "WHERE p.paymentTime BETWEEN :startDate AND :endDate " +
           "AND p.responseCode = '00' " +  // Chỉ tính các giao dịch thành công
           "GROUP BY FUNCTION('YEAR', p.paymentTime), FUNCTION('MONTH', p.paymentTime) " +
           "ORDER BY FUNCTION('YEAR', p.paymentTime) ASC, FUNCTION('MONTH', p.paymentTime) ASC")
    List<Object[]> getMonthlyRevenue(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
}
