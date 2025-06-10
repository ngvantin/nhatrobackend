package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.request.SearchInforRequest;
import com.example.nhatrobackend.DTO.response.SearchInforResponse;
import com.example.nhatrobackend.Entity.SearchInformation;
import com.example.nhatrobackend.DTO.request.SearchCriteriaDTO;
import com.example.nhatrobackend.Entity.Room;

import java.util.List;
import java.util.Optional;

public interface SearchInformationService {
    void saveSearchInformationByUserId(Integer userId, SearchInforRequest searchInforRequest); // Thay đổi tham số

    SearchInforResponse updateSearchInformationByUuid(Integer userId, SearchInforRequest searchInforRequest);
    SearchInforRequest getSearchInformationByUuid(Integer userId);

    /**
     * Tìm danh sách email của người dùng có thông tin tìm kiếm phù hợp với tiêu chí
     * @param searchCriteriaDTO Tiêu chí tìm kiếm
     * @return Danh sách email của người dùng phù hợp
     */
    List<String> findMatchingUserEmails(SearchCriteriaDTO searchCriteriaDTO);

    /**
     * Tìm danh sách email của người dùng có thông tin tìm kiếm phù hợp với thông tin phòng
     * @param room Thông tin phòng cần tìm kiếm
     * @return Danh sách email của người dùng phù hợp
     */
    List<String> findMatchingUserEmailsForRoom(Room room);

    /**
     * Kiểm tra xem người dùng đã có thông tin tìm kiếm hay chưa
     * @param userId ID của người dùng
     * @return true nếu người dùng đã có thông tin tìm kiếm, false nếu chưa có
     */
    boolean hasSearchInformation(Integer userId);
}
