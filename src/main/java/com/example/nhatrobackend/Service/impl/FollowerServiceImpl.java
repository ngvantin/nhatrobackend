package com.example.nhatrobackend.Service.impl;
import com.example.nhatrobackend.Entity.Follower;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Responsitory.FollowerRepository;
import com.example.nhatrobackend.Responsitory.UserRepository;
import com.example.nhatrobackend.Service.FollowerService;
import com.example.nhatrobackend.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowerServiceImpl implements FollowerService {

    private final FollowerRepository followerRepository;
    private final UserService userService;

    @Transactional
    @Override
    public String followUser(Integer followingUserId, Integer followedUserId) {
        if (followingUserId.equals(followedUserId)) {
            throw new IllegalArgumentException("Không thể tự theo dõi chính mình.");
        }

        User followingUser = userService.findByUserId(followingUserId);
        User followedUser = userService.findByUserId(followedUserId);

        if (!followerRepository.existsByFollowingUserAndFollowedUser(followingUser, followedUser)) {
            Follower follower = new Follower();
            follower.setFollowingUser(followingUser);
            follower.setFollowedUser(followedUser);
            followerRepository.save(follower);
            // Cập nhật quan hệ trong entity (optional, JPA có thể tự quản lý)
            followingUser.getFollowing().add(follower);
            followedUser.getFollowers().add(follower);
            return "Bạn đã theo dõi thành công";
        }

        return"Bạn đã theo dõi người dùng này rồi.";
    }

    @Transactional
    @Override
    public String unfollowUser(Integer followingUserId, Integer followedUserId) {
        User followingUser = userService.findByUserId(followingUserId);
        User followedUser = userService.findByUserId(followedUserId);

        if (followerRepository.existsByFollowingUserAndFollowedUser(followingUser, followedUser)) {
            followerRepository.deleteByFollowingUserAndFollowedUser(followingUser, followedUser);
            // Cập nhật quan hệ trong entity (optional, JPA có thể tự quản lý)
            followingUser.getFollowing().removeIf(f -> f.getFollowedUser().equals(followedUser));
            followedUser.getFollowers().removeIf(f -> f.getFollowingUser().equals(followingUser));
            // Có thể thêm log hoặc trả về thông báo thành công rõ ràng hơn
            return"Đã hủy theo dõi thành công.";
        }
        return"Bạn chưa theo dõi người dùng này.";
    }

    @Override
    public boolean isFollowing(Integer followingUserId, Integer followedUserId) {
        User followingUser = userService.findByUserId(followingUserId);
        User followedUser = userService.findByUserId(followedUserId);
        return followerRepository.existsByFollowingUserAndFollowedUser(followingUser, followedUser);
    }
}
