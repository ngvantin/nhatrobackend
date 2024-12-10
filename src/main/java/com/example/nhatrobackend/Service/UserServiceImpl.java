package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.UserDetailDTO;
import com.example.nhatrobackend.DTO.UserInformationDTO;
import com.example.nhatrobackend.Entity.Account;
import com.example.nhatrobackend.Entity.Field.LandlordStatus;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Mapper.UserMapper;
import com.example.nhatrobackend.Responsitory.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AccountService accountService;

    @Override
    public boolean getApprovedUserByUuid(String userUuid) {
        Optional<User> optionalUser = userRepository.findByUserUuid(userUuid);
        return optionalUser.isPresent() && optionalUser.get().getIsLandlordActivated() == LandlordStatus.APPROVED;
    }

    @Override
    public User findByUserUuid(String userUuid) {
        Optional<User> optionalUser = userRepository.findByUserUuid(userUuid);

        // Nếu tìm thấy Post, chuyển đổi sang PostDetailResponseDTO
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new EntityNotFoundException("Không tìm thấy User ID: " + userUuid);
        }
    }

    @Override
    public User getUserByUuid(String userUuid) {
        return userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new RuntimeException("User not found with UUID: " + userUuid));
    }

    @Override
    public UserInformationDTO getUserInformationByUuid(String userUuid) {
        // Tìm userId từ userUuid
        Integer userId = userRepository.findByUserUuid(userUuid)
                .map(User::getUserId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với UUID đã cung cấp."));

        // Gọi AccountService để lấy `Account`
        Account account = accountService.findAccountByUserId(userId);
        UserInformationDTO userInformationDTO = new UserInformationDTO();
        userInformationDTO.setRole(account.getRole());
        userInformationDTO.setFullName(account.getUser().getFullName());

        return userInformationDTO;
    }
}
