package com.example.nhatrobackend.Rest;

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

    @GetMapping("/provinces")
    public ResponseEntity<List<Province>> getAllProvinces() {
        return ResponseEntity.ok(administrativeService.getAllProvinces());
    }

    @GetMapping("/districts/{provinceCode}")
    public ResponseEntity<List<District>> getDistrictsByProvince(@PathVariable String provinceCode) {
        return ResponseEntity.ok(administrativeService.getDistrictsByProvince(provinceCode));
    }

    @GetMapping("/wards/{districtCode}")
    public ResponseEntity<List<Ward>> getWardsByDistrict(@PathVariable String districtCode) {
        return ResponseEntity.ok(administrativeService.getWardsByDistrict(districtCode));
    }
}

