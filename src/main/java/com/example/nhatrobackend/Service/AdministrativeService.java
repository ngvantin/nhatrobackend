package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.DistrictDTO;
import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.ProvinceDTO;
import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.WardDTO;
import com.example.nhatrobackend.Entity.Administrative.District;
import com.example.nhatrobackend.Entity.Administrative.Province;
import com.example.nhatrobackend.Entity.Administrative.Ward;

import java.util.List;

public interface AdministrativeService {
    List<String> getAllProvinces();
    List<String> getDistrictsByProvince(String provinceCode);
    List<String> getWardsByDistrict(String districtCode);
}
