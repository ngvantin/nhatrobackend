package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.DistrictDTO;
import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.ProvinceDTO;
import com.example.nhatrobackend.DTO.AdministrativeServiceDTO.WardDTO;
import com.example.nhatrobackend.Entity.Administrative.District;
import com.example.nhatrobackend.Entity.Administrative.Province;
import com.example.nhatrobackend.Entity.Administrative.Ward;
import com.example.nhatrobackend.Service.AdministrativeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administrative")
public class AdministrativeController {

    private final AdministrativeService administrativeService;

    public AdministrativeController(AdministrativeService administrativeService) {
        this.administrativeService = administrativeService;
    }

    // API lấy tất cả các tỉnh
    @GetMapping("/provinces")
    public ResponseEntity<List<String>> getAllProvinces(@RequestParam(required = false) String keyword) {
        List<String> provinces = administrativeService.getAllProvinces(keyword);
        return ResponseEntity.ok(provinces);
    }

    // API lấy các quận theo mã tỉnh
    @GetMapping("/districts/{provinceName}")
    public ResponseEntity<List<String>> getDistrictsByProvince(
            @PathVariable String provinceName,
            @RequestParam(required = false) String keyword) {
        List<String> districts = administrativeService.getDistrictsByProvince(provinceName, keyword);
        return ResponseEntity.ok(districts);
    }

    // Lấy tên các phường theo mã quận
    @GetMapping("/wards/{districtFullName}")
    public ResponseEntity<List<String>> getWardsByDistrict(
            @PathVariable String districtFullName,
            @RequestParam(required = false) String keyword) {
        List<String> wards = administrativeService.getWardsByDistrict(districtFullName, keyword);
        return ResponseEntity.ok(wards);
    }
}


