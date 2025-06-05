package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.DistrictDTO;
import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.ProvinceDTO;
import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.WardDTO;
import com.example.nhatrobackend.Entity.Administrative.District;
import com.example.nhatrobackend.Entity.Administrative.Province;
import com.example.nhatrobackend.Entity.Administrative.Ward;
import com.example.nhatrobackend.Mapper.Administrative.DistrictMapper;
import com.example.nhatrobackend.Mapper.Administrative.ProvinceMapper;
import com.example.nhatrobackend.Mapper.Administrative.WardMapper;
import com.example.nhatrobackend.Mapper.RoomMapper;
import com.example.nhatrobackend.Responsitory.Administrative.DistrictRepository;
import com.example.nhatrobackend.Responsitory.Administrative.ProvinceRepository;
import com.example.nhatrobackend.Responsitory.Administrative.WardRepository;
import com.example.nhatrobackend.Service.AdministrativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdministrativeServiceImpl implements AdministrativeService {
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final ProvinceMapper provinceMapper;
    private final DistrictMapper districtMapper;
    private final WardMapper wardMapper;
//    public List<ProvinceDTO> getAllProvinces() {
//        return provinceRepository.findAll();
//    }
//
//    public List<DistrictDTO> getDistrictsByProvince(String provinceCode) {
//        return districtRepository.findByProvince_Code(provinceCode);
//    }
//
//    public List<WardDTO> getWardsByDistrict(String districtCode) {
//        return wardRepository.findByDistrict_Code(districtCode);
//    }

    // Lấy tất cả các tỉnh (Province)
    @Override
    public List<String> getAllProvinces(String keyword) {
        List<Province> provinces;
        if (keyword != null && !keyword.trim().isEmpty()) {
            provinces = provinceRepository.findByNameContainingIgnoreCaseAndAccents(keyword);
        } else {
            provinces = provinceRepository.findAll();
        }
        return provinces.stream()
                .map(Province::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    // Lấy tên các quận (District) theo mã tỉnh (provinceCode)
    @Override
    public List<String> getDistrictsByProvince(String provinceName, String keyword) {
        List<District> districts;
        if (keyword != null && !keyword.trim().isEmpty()) {
            districts = districtRepository.findByProvinceNameAndFullNameContainingIgnoreCaseAndAccents(provinceName, keyword);
        } else {
            districts = districtRepository.findByProvince_Name(provinceName);
        }
        return districts.stream()
                .map(District::getFullName)
                .sorted()
                .collect(Collectors.toList());
    }

    // Lấy tên các phường (Ward) theo mã quận (districtCode)
    @Override
    public List<String> getWardsByDistrict(String districtFullName, String keyword) {
        List<Ward> wards;
        if (keyword != null && !keyword.trim().isEmpty()) {
            wards = wardRepository.findByDistrictFullNameAndFullNameContainingIgnoreCaseAndAccents(districtFullName, keyword);
        } else {
            wards = wardRepository.findByDistrict_FullName(districtFullName);
        }
        return wards.stream()
                .map(Ward::getFullName)
                .sorted()
                .collect(Collectors.toList());
    }

}
