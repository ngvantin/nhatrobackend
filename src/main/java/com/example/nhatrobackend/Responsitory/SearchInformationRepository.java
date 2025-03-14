package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.SearchInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchInformationRepository extends JpaRepository<SearchInformation, Long> {
    Optional<SearchInformation> findByUser_UserId(Integer userId);
    Optional<SearchInformation> findBySearchInforUuid(String searchInforUuid);

    // Thêm các phương thức truy vấn cần thiết
}
