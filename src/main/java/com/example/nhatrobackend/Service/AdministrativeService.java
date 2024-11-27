package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.Entity.Administrative.District;
import com.example.nhatrobackend.Entity.Administrative.Province;
import com.example.nhatrobackend.Entity.Administrative.Ward;

import java.util.List;

public interface AdministrativeService {
    List<Province> getAllProvinces();
    List<District> getDistrictsByProvince(String provinceCode);
    List<Ward> getWardsByDistrict(String districtCode);
}
