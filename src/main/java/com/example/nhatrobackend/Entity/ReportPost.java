package com.example.nhatrobackend.Entity;

import com.example.nhatrobackend.Entity.Field.ReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "report_post")
public class ReportPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "admin_response", columnDefinition = "TEXT")
    private String adminResponse;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;


}

