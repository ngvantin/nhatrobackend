package com.example.nhatrobackend.Responsitory.Administrative;

import com.example.nhatrobackend.Entity.Administrative.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProvinceRepository extends JpaRepository<Province, String> {
    @Query("SELECT p FROM Province p WHERE LOWER(REPLACE(REPLACE(REPLACE(REPLACE(p.name, ' ', ''), ',', ''), '.', ''), '-', '')) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.name")
    List<Province> findByNameContainingIgnoreCaseAndAccents(@Param("keyword") String keyword);
}
