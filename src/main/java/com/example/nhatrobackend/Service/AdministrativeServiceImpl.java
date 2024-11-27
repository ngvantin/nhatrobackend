package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.Entity.Administrative.District;
import com.example.nhatrobackend.Entity.Administrative.Province;
import com.example.nhatrobackend.Entity.Administrative.Ward;
import com.example.nhatrobackend.Responsitory.Administrative.DistrictRepository;
import com.example.nhatrobackend.Responsitory.Administrative.ProvinceRepository;
import com.example.nhatrobackend.Responsitory.Administrative.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministrativeServiceImpl implements AdministrativeService{
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }

    public List<District> getDistrictsByProvince(String provinceCode) {
        return districtRepository.findByProvince_Code(provinceCode);
    }

    public List<Ward> getWardsByDistrict(String districtCode) {
        return wardRepository.findByDistrict_Code(districtCode);
    }
}
