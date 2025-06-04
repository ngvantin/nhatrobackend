package com.example.nhatrobackend.Responsitory.Administrative;

import com.example.nhatrobackend.Entity.Administrative.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WardRepository extends JpaRepository<Ward, String> {
    List<Ward> findByDistrict_Code(String districtCode);
    List<Ward> findByDistrict_FullName(String districtFullName);

    @Query("SELECT w FROM Ward w WHERE w.district.fullName = :districtFullName AND " +
           "LOWER(REPLACE(REPLACE(REPLACE(REPLACE(w.fullName, ' ', ''), ',', ''), '.', ''), '-', '')) " +
           "LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY w.fullName")
    List<Ward> findByDistrictFullNameAndFullNameContainingIgnoreCaseAndAccents(
            @Param("districtFullName") String districtFullName,
            @Param("keyword") String keyword);
}
