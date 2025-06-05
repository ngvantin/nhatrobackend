package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.ReportImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportImageRepository extends JpaRepository<ReportImage, Integer> {
} 