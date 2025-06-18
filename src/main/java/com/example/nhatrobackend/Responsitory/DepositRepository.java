package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.Deposit;
import com.example.nhatrobackend.Entity.Field.DepositStatus;
import com.example.nhatrobackend.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Integer> {
    Page<Deposit> findByUser_UserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    List<Deposit> findByPost_User_UserIdOrderByCreatedAtDesc(Integer userId);
    Page<Deposit> findByUser_UserId(Integer userId, Pageable pageable);
    
    @Query("SELECT DISTINCT d.post FROM Deposit d WHERE d.post.user.userId = :currentUserId AND d.user.userId != :currentUserId")
    Page<Post> findPostsWithDepositsByOtherUsers(@Param("currentUserId") Integer currentUserId, Pageable pageable);
    
    List<Deposit> findByPost_PostId(Integer postId);
    
    Page<Deposit> findByStatus(DepositStatus status, Pageable pageable);
    
    @Query("SELECT d FROM Deposit d WHERE d.status IN :statuses")
    Page<Deposit> findByStatusIn(@Param("statuses") List<DepositStatus> statuses, Pageable pageable);
} 