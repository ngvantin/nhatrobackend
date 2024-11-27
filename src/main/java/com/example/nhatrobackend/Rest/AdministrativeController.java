package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.DistrictDTO;
import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.ProvinceDTO;
import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.WardDTO;
import com.example.nhatrobackend.Entity.Administrative.District;
import com.example.nhatrobackend.Entity.Administrative.Province;
import com.example.nhatrobackend.Entity.Administrative.Ward;
import com.example.nhatrobackend.Service.AdministrativeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/administrative")
public class AdministrativeController {

    private final AdministrativeService administrativeService;

    public AdministrativeController(AdministrativeService administrativeService) {
        this.administrativeService = administrativeService;
    }

    // API lấy tất cả các tỉnh
    // API lấy tất cả các tỉnh dưới dạng mảng tên tỉnh
    @GetMapping("/provinces")
    public ResponseEntity<List<String>> getAllProvinces() {
        List<String> provinces = administrativeService.getAllProvinces();

        // Trả về ResponseEntity với mã trạng thái 200 OK và dữ liệu
        return ResponseEntity.ok(provinces);
    }

    // API lấy các quận theo mã tỉnh
    // Lấy tên các quận (District) theo mã tỉnh (provinceCode)
    @GetMapping("/districts/{provinceCode}")
    public ResponseEntity<List<String>> getDistrictsByProvince(@PathVariable String provinceCode) {
        List<String> districts = administrativeService.getDistrictsByProvince(provinceCode);
        return ResponseEntity.ok(districts);  // Trả về danh sách tên quận
    }

    // Lấy tên các phường (Ward) theo mã quận (districtCode)
    @GetMapping("/wards/{districtCode}")
    public ResponseEntity<List<String>> getWardsByDistrict(@PathVariable String districtCode) {
        List<String> wards = administrativeService.getWardsByDistrict(districtCode);
        return ResponseEntity.ok(wards);  // Trả về danh sách tên phường
    }
}


