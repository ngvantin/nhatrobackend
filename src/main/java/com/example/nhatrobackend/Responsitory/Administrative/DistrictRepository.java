package com.example.nhatrobackend.Responsitory.Administrative;

import com.example.nhatrobackend.Entity.Administrative.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, String> {
    List<District> findByProvince_Code(String provinceCode);
    List<District> findByProvince_Name(String provinceName);

    @Query("SELECT d FROM District d WHERE d.province.name = :provinceName AND " +
           "LOWER(REPLACE(REPLACE(REPLACE(REPLACE(d.fullName, ' ', ''), ',', ''), '.', ''), '-', '')) " +
           "LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY d.fullName")
    List<District> findByProvinceNameAndFullNameContainingIgnoreCaseAndAccents(
            @Param("provinceName") String provinceName,
            @Param("keyword") String keyword);
}
