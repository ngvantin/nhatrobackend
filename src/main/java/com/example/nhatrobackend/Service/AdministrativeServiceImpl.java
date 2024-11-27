package com.example.nhatrobackend.Service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdministrativeServiceImpl implements AdministrativeService{
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
    public List<String> getAllProvinces() {
        List<Province> provinces = provinceRepository.findAll();
        return provinces.stream()
                .map(Province::getName)  // Lấy tên tỉnh từ đối tượng Province
                .collect(Collectors.toList());  // Trả về danh sách tên tỉnh

    }

    // Lấy tên các quận (District) theo mã tỉnh (provinceCode)
    public List<String> getDistrictsByProvince(String provinceName) {
        List<District> districts = districtRepository.findByProvince_Name(provinceName);
        return districts.stream()
                .map(District::getFullName)  // Lấy tên quận từ đối tượng District
                .collect(Collectors.toList());  // Trả về danh sách tên quận
    }

    // Lấy tên các phường (Ward) theo mã quận (districtCode)
    public List<String> getWardsByDistrict(String districtFullName) {
        List<Ward> wards = wardRepository.findByDistrict_FullName(districtFullName);
        return wards.stream()
                .map(Ward::getFullName)  // Lấy tên phường từ đối tượng Ward
                .collect(Collectors.toList());  // Trả về danh sách tên phường
    }

}
