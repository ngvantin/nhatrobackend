package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import com.example.nhatrobackend.Entity.Field.PostStatus;
import com.example.nhatrobackend.Entity.Post;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository  extends JpaRepository<Post, Integer> {
    // Thêm phương thức tìm kiếm theo postUuid
    Optional<Post> findByPostUuid(String postUuid);
    @Query("SELECT p FROM Post p JOIN p.room r WHERE " +
            "(:minPrice IS NULL OR r.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR r.price <= :maxPrice) AND " +
            "(:minArea IS NULL OR r.area >= :minArea) AND " +
            "(:maxArea IS NULL OR r.area <= :maxArea) AND " +
            "(:furnitureStatus IS NULL OR r.furnitureStatus = :furnitureStatus) AND " +
            "(r.city = :city OR :city IS NULL) AND " +
            "(r.district = :district OR :district IS NULL) AND " +
            "(r.ward = :ward OR :ward IS NULL) AND " +
            "(:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> findPostsByRoomCriteria(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minArea") Double minArea,
            @Param("maxArea") Double maxArea,
            @Param("furnitureStatus") FurnitureStatus furnitureStatus,
            @Param("city") String city,
            @Param("district") String district,
            @Param("ward") String ward,
            @Param("keyword") String keyword,
            Pageable pageable); // Thêm tham số Pageable

    void deleteByPostId(int postId);

    // Lọc bài viết có trạng thái APPROVED và userId tương ứng
    Page<Post> findByStatusAndUser_UserId(PostStatus status, Integer userId, Pageable pageable);

//    @Query("SELECT p FROM Post p JOIN p.room r WHERE " +
//            "(:keyword IS NULL OR LOWER(r.city) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
//            "(:keyword IS NULL OR LOWER(r.district) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
//            "(:keyword IS NULL OR LOWER(r.ward) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
//            "(:keyword IS NULL OR LOWER(r.street) LIKE LOWER(CONCAT('%', :keyword, '%')))")
//    Page<Post> findPostsByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.room r WHERE " +
            "(:keyword IS NULL OR LOWER(REPLACE(REPLACE(REPLACE(REPLACE(r.city, ' ', ''), ',', ''), '.', ''), '-', '')) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:keyword IS NULL OR LOWER(REPLACE(REPLACE(REPLACE(REPLACE(r.district, ' ', ''), ',', ''), '.', ''), '-', '')) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:keyword IS NULL OR LOWER(REPLACE(REPLACE(REPLACE(REPLACE(r.ward, ' ', ''), ',', ''), '.', ''), '-', '')) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:keyword IS NULL OR LOWER(REPLACE(REPLACE(REPLACE(REPLACE(r.street, ' ', ''), ',', ''), '.', ''), '-', '')) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> findPostsByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Lọc bài post theo trạng thái và phân trang
    Page<Post> findByStatus(PostStatus status, Pageable pageable);
    List<Post> findByRoom_CityAndRoom_DistrictAndRoom_WardOrderByCreatedAtAsc(String city, String district, String ward);

}
