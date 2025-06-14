package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.DepositLandlordComplaintImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositLandlordComplaintImageRepository extends JpaRepository<DepositLandlordComplaintImage, Integer> {
} 