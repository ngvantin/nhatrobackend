package com.example.nhatrobackend.Responsitory.Administrative;

import com.example.nhatrobackend.Entity.Administrative.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WardRepository extends JpaRepository<Ward, String> {
    List<Ward> findByDistrict_Code(String districtCode);
}
