package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.DTO.request.SearchInforRequest;
import com.example.nhatrobackend.DTO.response.SearchInforResponse;
import com.example.nhatrobackend.Entity.SearchInformation;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.SearchInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search-information")
@RequiredArgsConstructor
public class SearchInformationController {
    private final SearchInformationService searchInformationService;
    private final AuthenticationFacade authenticationFacade;

    @PostMapping("/create") // API để lưu thông tin tìm kiếm
    public ResponseEntity<ResponseWrapper<Void>> saveUserSearchInformation(
            @RequestBody SearchInforRequest searchInforRequest) {
        Integer userId = authenticationFacade.getCurrentUserId();
        searchInformationService.saveSearchInformationByUserId(userId, searchInforRequest);

        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Thông tin tìm kiếm đã được lưu.")
                .build());
    }

    @GetMapping("/detail")
    public ResponseEntity<ResponseWrapper<SearchInforRequest>> getSearchInformation() {
        Integer userId = authenticationFacade.getCurrentUserId();
        SearchInforRequest searchInforRequest = searchInformationService.getSearchInformationByUuid(userId);

        return ResponseEntity.ok(ResponseWrapper.<SearchInforRequest>builder()
                .status("success")
                .data(searchInforRequest)
                .message("Thông tin tìm kiếm.")
                .build());
    }

    @PutMapping("/update/{searchInforUuid}")
    public ResponseEntity<ResponseWrapper<SearchInforResponse>> updateSearchInformation(
            @PathVariable String searchInforUuid,
            @RequestBody SearchInforRequest searchInforRequest) {
        SearchInforResponse updatedSearchInforResponse = searchInformationService.updateSearchInformationByUuid(searchInforUuid, searchInforRequest);

        return ResponseEntity.ok(ResponseWrapper.<SearchInforResponse>builder()
                .status("success")
                .data(updatedSearchInforResponse)
                .message("Thông tin tìm kiếm đã được cập nhật.")
                .build());
    }
}