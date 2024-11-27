package com.example.nhatrobackend.Responsitory.Administrative;

import com.example.nhatrobackend.Entity.Administrative.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, String> {
    List<District> findByProvince_Code(String provinceCode);
    List<District> findByProvince_Name(String provinceName);

}
