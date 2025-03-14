package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.request.SearchInforRequest;
import com.example.nhatrobackend.DTO.response.SearchInforResponse;
import com.example.nhatrobackend.Entity.SearchInformation;

import java.util.Optional;

public interface SearchInformationService {
    void saveSearchInformationByUserId(Integer userId, SearchInforRequest searchInforRequest); // Thay đổi tham số

    SearchInforResponse updateSearchInformationByUuid(String searchInforUuid, SearchInforRequest searchInforRequest);
    SearchInforRequest getSearchInformationByUuid(Integer userId);
}
