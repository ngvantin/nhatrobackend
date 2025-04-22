package com.example.nhatrobackend.Service;

public interface FollowerService {
    public String followUser(Integer followingUserId, String followedUserUuid);
    public String unfollowUser(Integer followingUserId, String followedUserUuid);
    boolean isFollowing(Integer followingUserId, String followedUserUuid);

}
