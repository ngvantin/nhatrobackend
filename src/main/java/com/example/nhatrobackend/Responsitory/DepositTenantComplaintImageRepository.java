package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.DepositTenantComplaintImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositTenantComplaintImageRepository extends JpaRepository<DepositTenantComplaintImage, Integer> {
} 