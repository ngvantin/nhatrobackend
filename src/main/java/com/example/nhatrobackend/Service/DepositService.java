package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.DTO.request.DepositRequest;
import com.example.nhatrobackend.DTO.request.DepositRefundRequest;
import com.example.nhatrobackend.DTO.response.VNPayResponse;
import com.example.nhatrobackend.Entity.Field.DepositStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepositService {
    VNPayResponse createDepositPayment(DepositRequest depositRequest, HttpServletRequest request, Integer currentUserId);
    String processDepositCallback(HttpServletRequest request);
    Object getDepositDetails(Integer depositId, Integer currentUserId);
    Object confirmDeposit(Integer depositId, Integer currentUserId, Boolean isConfirmed);
    Page<PostWithDepositDTO> getDepositedPosts(Integer userId, Pageable pageable);
    //    Page<DepositResponseDTO> getDepositsByUser(Integer userId, Pageable pageable);
    Page<PostResponseDTO> getPostsWithDepositsByOtherUsers(Integer currentUserId, Pageable pageable);
    List<UserDepositDTO> getUsersWithDepositsByPostId(Integer postId);
    DepositDetailDTO getDepositDetailsById(Integer depositId);
    String confirmByTenant(Integer depositId, Integer currentUserId);
    String confirmByLandlord(Integer depositId, Integer currentUserId);
    String complaintByTenant(Integer depositId, Integer currentUserId, DepositComplaintRequestDTO requestDTO);
    String complaintByLandlord(Integer depositId, Integer currentUserId, DepositComplaintRequestDTO requestDTO);
    DepositFullDetailDTO getFullDepositDetails(Integer depositId);
    Page<DepositStatusDTO> getDepositsByStatus(DepositStatus status, Pageable pageable);
    String refundDeposit(DepositRefundRequest request, HttpServletRequest httpRequest);
    String refundDepositToTenant(Integer depositId);
    String payCommissionToLandlord(Integer depositId);
} 