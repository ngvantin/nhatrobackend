package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.ReportPost;
import com.example.nhatrobackend.Entity.Field.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface ReportPostRepository extends JpaRepository<ReportPost, Integer> {
    @Query("SELECT rp.post FROM ReportPost rp")
    Page<Post> findAllReportedPostsOrderByCreatedAtDesc(Pageable pageable);

    Optional<ReportPost> findByReportId(Integer reportId);
    
    Page<ReportPost> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);
}