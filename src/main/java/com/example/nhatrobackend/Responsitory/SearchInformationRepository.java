package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import com.example.nhatrobackend.Entity.SearchInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SearchInformationRepository extends JpaRepository<SearchInformation, Long> {
    Optional<SearchInformation> findByUser_UserId(Integer userId);
    Optional<SearchInformation> findBySearchInforUuid(String searchInforUuid);

    @Query("SELECT si FROM SearchInformation si WHERE " +
           "(:price IS NULL OR (si.minPrice <= :price AND si.maxPrice >= :price)) AND " +
           "(:area IS NULL OR (si.minArea <= :area AND si.maxArea >= :area)) AND " +
           "(:furnitureStatus IS NULL OR si.furnitureStatus = :furnitureStatus) AND " +
           "(:city IS NULL OR si.city = :city) AND " +
           "(:district IS NULL OR si.district = :district) AND " +
           "(:ward IS NULL OR si.ward = :ward)")
    List<SearchInformation> findMatchingSearchInformation(
            @Param("price") Double price,
            @Param("area") Double area,
            @Param("furnitureStatus") FurnitureStatus furnitureStatus,
            @Param("city") String city,
            @Param("district") String district,
            @Param("ward") String ward
    );
}
