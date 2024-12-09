package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.ReportPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPostRepository extends JpaRepository<ReportPost, Integer> {
    // Các truy vấn nếu cần
}