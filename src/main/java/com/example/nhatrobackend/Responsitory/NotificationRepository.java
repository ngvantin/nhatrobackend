package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Repository để tương tác với bảng notifications trong database.
 * Cung cấp các phương thức truy vấn và cập nhật thông báo.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /**
     * Lấy danh sách thông báo của một user
     * @param userId ID của user
     * @param pageable Thông tin phân trang
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    /**
     * Đếm số thông báo chưa đọc của user
     * @param userId ID của user
     */
    long countByUserIdAndIsReadFalse(Integer userId);

    /**
     * Đánh dấu tất cả thông báo của user là đã đọc
     * @param userId ID của user
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = :isRead WHERE n.userId = :userId")
    void updateIsReadByUserIdAndIsRead(Integer userId, boolean isRead);

    /**
     * Đánh dấu thông báo đã đọc
     * @param notificationId ID của thông báo
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId")
    void markNotificationAsRead(Long notificationId);

    /**
     * Lấy thông báo theo loại và trạng thái
     * @param type Loại thông báo
     * @param isRead Trạng thái đã đọc
     */
    Page<Notification> findByTypeAndIsRead(String type, boolean isRead, Pageable pageable);

    /**
     * Xóa thông báo cũ hơn X ngày
     */
    @Modifying
    @Transactional
    void deleteByCreatedAtBefore(LocalDateTime date);
}