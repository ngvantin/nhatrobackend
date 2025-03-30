package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.Follower;
import com.example.nhatrobackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {

    Optional<Follower> findByFollowingUserAndFollowedUser(User followingUser, User followedUser);

    boolean existsByFollowingUserAndFollowedUser(User followingUser, User followedUser);

    void deleteByFollowingUserAndFollowedUser(User followingUser, User followedUser);
}
