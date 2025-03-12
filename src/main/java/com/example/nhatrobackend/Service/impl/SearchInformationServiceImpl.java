package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.DTO.request.SearchInforRequest;
import com.example.nhatrobackend.DTO.response.SearchInforResponse;
import com.example.nhatrobackend.Entity.*;
import com.example.nhatrobackend.Mapper.SearchInformationMapper;
import com.example.nhatrobackend.Responsitory.SearchInformationRepository;
import com.example.nhatrobackend.Service.SearchInformationService;
import com.example.nhatrobackend.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchInformationServiceImpl implements SearchInformationService {
    private final SearchInformationRepository searchInformationRepository;
    private final UserService userService;
    private final SearchInformationMapper searchInformationMapper;

    @Override
    public void saveSearchInformationByUserId(Integer userId, SearchInforRequest searchInforRequest) { // Thay đổi tham số
        User user = userService.findByUserId(userId);
        SearchInformation searchInformation = searchInformationMapper.toSearchInformation(searchInforRequest);
        searchInformation.setUser(user);
        searchInformationRepository.save(searchInformation);
    }
    @Override
    public SearchInforRequest getSearchInformationByUuid(String searchInforUuid) {
        log.info("searchInforUuid " + searchInforUuid);
        SearchInformation searchInformation = findByUuid(searchInforUuid);
        log.info("SearchInformation :" + searchInformation.getMaxArea());
        return searchInformationMapper.toSearchInforRequest(searchInformation);
    }

    @Override
    public SearchInforResponse updateSearchInformationByUuid(String searchInforUuid, SearchInforRequest searchInforRequest) {
        SearchInformation existingSearchInformation = findByUuid(searchInforUuid);
        searchInformationMapper.updateSearchInformationFromDTO(searchInforRequest, existingSearchInformation);
        SearchInformation updatedSearchInformation = searchInformationRepository.save(existingSearchInformation);
        return searchInformationMapper.toSearchInforResponse(updatedSearchInformation);
    }

    private SearchInformation findByUuid(String searchInforUuid) {
        return searchInformationRepository.findBySearchInforUuid(searchInforUuid)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin tìm kiếm với UUID: " + searchInforUuid));
    }
}
